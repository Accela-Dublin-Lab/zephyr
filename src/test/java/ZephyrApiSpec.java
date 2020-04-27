import com.accela.zephyr.ApiController;
import com.accela.zephyr.TimeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApiController.class })
@WebMvcTest(ApiController.class)
public class ZephyrApiSpec {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TimeService service;

    @Test
    @DisplayName("Timestamp API call succeeds")
    public void testTimestampRequest() throws Exception {
        final String expectDateTime = "2020-01-01T12:35:01Z";
        final ZonedDateTime ts = ZonedDateTime.parse(expectDateTime);

        given(service.timestamp()).willReturn(ts);

        mvc.perform(get("/timestamp")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp", is(expectDateTime)));
    }

}