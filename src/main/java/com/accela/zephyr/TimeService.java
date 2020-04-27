package com.accela.zephyr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RefreshScope
public class TimeService {

    @Value("${service.timezone}")
    private String timezone;

    public ZonedDateTime timestamp() {
        return ZonedDateTime.now(ZoneId.of(timezone));
    }
}
