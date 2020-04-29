import com.accela.zephyr.TimeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TimeService.class })
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@TestPropertySource(properties = {"service.timezone=Europe/Dublin"})
public class TimeServiceSpec {

    @Autowired
    private TimeService service;

    @Test
    @DisplayName("Timestamp in correct time-zone")
    public void testTimestamp() {
        assertEquals(ZoneId.of("Europe/Dublin"), service.timestamp().getZone());
    }

}