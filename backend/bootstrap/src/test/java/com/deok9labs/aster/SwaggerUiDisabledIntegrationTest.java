package com.deok9labs.aster;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "ASTER_OPENAPI_ENABLED=false")
class SwaggerUiDisabledIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    @Test
    void keepsOpenApiEndpointsDisabledWhenNotEnabled() throws IOException, InterruptedException {
        assertEquals(404, TestHttpClient.get(port, "/v3/api-docs").statusCode());
        assertEquals(404, TestHttpClient.get(port, "/swagger-ui/index.html").statusCode());
    }
}
