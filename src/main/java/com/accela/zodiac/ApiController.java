package com.accela.zodiac;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import javax.naming.ServiceUnavailableException;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@RestController
public class ApiController {
    private static final JsonParser parser = JsonParserFactory.getJsonParser();
    private final WebClient webClient;
    private final Supplier<Optional<URI>> serviceUrl;

    @Autowired
    public ApiController(final DiscoveryClient discoveryClient) {
        serviceUrl = () ->
            discoveryClient.getInstances("zephyr-service")
                    .stream()
                    .findFirst()
                    .map(ServiceInstance::getUri);
        this.webClient = WebClient.create();
    }

    @GetMapping(value = "/forecast", produces = MediaType.TEXT_HTML_VALUE)
    public String zephyrCall() throws ServiceUnavailableException {
        return serviceUrl.get()
                .map(uri -> uri.resolve("/timestamp"))
                .map(uri -> webClient.get().uri(uri).accept(MediaType.APPLICATION_JSON).exchange().block())
                .filter(Objects::nonNull)
                .map(response -> response.bodyToMono(String.class).block())
                .map(parseResponse)
                .map(formatReply)
                .orElseThrow(ServiceUnavailableException::new);
    }

    private static final Function<String,ZonedDateTime> parseResponse = response -> {
        final Map<String, Object> map = parser.parseMap(response);
        return ZonedDateTime.parse(map.get("timestamp").toString());
    };

    private static final Function<ZonedDateTime,String> formatReply = ts -> {
        final String text = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).format(ts);
        return "<html><body><h1>Zephyr will blow at...</h1><h2>"+text+"</h2></body></html>";
    };

    @GetMapping("/actuator/health")
    public ResponseEntity<String> healthChecker() {
        return new ResponseEntity<>("zodiac health ok", HttpStatus.OK);
    }

}