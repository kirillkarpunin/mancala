package com.bol.actuator;

import com.bol.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;


public class HealthTest extends AbstractControllerTest {

    @Test
    public void shouldReturnStatusUp() {
        var response = restTemplate.getForEntity("http://localhost:%s/actuator/health".formatted(port), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("{\"status\":\"UP\"}");
    }
}
