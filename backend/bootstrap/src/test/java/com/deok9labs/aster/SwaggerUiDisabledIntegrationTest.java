package com.deok9labs.aster;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
        assertEquals(404, get("/v3/api-docs").statusCode());
        assertEquals(404, get("/swagger-ui/index.html").statusCode());
    }

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .GET()
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}
