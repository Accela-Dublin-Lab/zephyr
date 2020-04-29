import com.accela.zodiac.ApiController;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApiController.class })
@WebMvcTest(ApiController.class)
@TestPropertySource(properties = "spring.cloud.consul.config.enabled=false")
public class ZodiacApiSpec {
    private static final String testEndpoint = "http://localhost:8080";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SimpleDiscoveryClient discoveryClient;

    @MockBean
    private ServiceInstance zephyrService;

    private static WireMockServer wireMockServer;

    @BeforeAll
    private static void createServer() {
        wireMockServer = new WireMockServer(wireMockConfig().port(8080));
        wireMockServer.start();
    }

    @AfterAll
    private static void stopServer() {
        wireMockServer.shutdown();
    }

    @Test
    @DisplayName("Forecast API call succeeds")
    public void testForecastRequest() throws Exception {
        stubFor(get(urlEqualTo("/timestamp"))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"timestamp\": \"2020-01-01T12:35:01Z\"}")));

        when(zephyrService.getUri())
                .thenReturn(URI.create(testEndpoint));
        when(discoveryClient.getInstances(anyString()))
                .thenReturn(Collections.singletonList(zephyrService));

        final String expectResponse =
                  "<html><body><h1>Zephyr will blow at...</h1>" +
                  "<h2>Wednesday, January 1, 2020 12:35:01 PM Z</h2></body></html>";

        mvc.perform(MockMvcRequestBuilders.get("/forecast")
                .contentType(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(expectResponse));
    }

    @Test
    @DisplayName("Health check API call succeeds")
    public void checkHealthCheck() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("zodiac health ok"));
    }

}