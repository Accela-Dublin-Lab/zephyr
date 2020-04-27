package com.accela.zephyr;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Zephyr {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Zephyr.class)
                .properties("spring.cloud.bootstrap.name:zephyr-bootstrap",
                            "spring.config.name:zephyr-application")
                .run(args);
    }
}
