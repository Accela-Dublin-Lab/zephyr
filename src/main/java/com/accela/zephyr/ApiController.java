package com.accela.zephyr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    private final TimeService service;

    ApiController(final TimeService service) {
        this.service = service;
    }

    @GetMapping(value = "/timestamp", produces = MediaType.APPLICATION_JSON_VALUE)
    public String timestamp() {
        return "{\"timestamp\":\""+service.timestamp()+"\"}";
    }

    @GetMapping("/actuator/health")
    public ResponseEntity<String> healthChecker() {
        return new ResponseEntity<>("zephyr health ok", HttpStatus.OK);
    }
}