package com.deok9labs.aster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SwaggerUiIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    @Test
    void servesOpenApiDocument() throws IOException, InterruptedException {
        HttpResponse<String> response = TestHttpClient.get(port, "/v3/api-docs");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"/hello\""));
    }

    @Test
    void servesSwaggerUi() throws IOException, InterruptedException {
        HttpResponse<String> response = TestHttpClient.get(port, "/swagger-ui/index.html");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Swagger UI"));
    }

    @Test
    void servesHelloEndpoint() throws IOException, InterruptedException {
        HttpResponse<String> response = TestHttpClient.get(port, "/hello");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"message\":\"Hello World\""));
    }
}
