package com.accela.zodiac;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Zodiac {
    public static void main(final String[] args) {
        new SpringApplicationBuilder(Zodiac.class)
                .properties("spring.config.name:zodiac-bootstrap")
                .run(args);
    }

}
