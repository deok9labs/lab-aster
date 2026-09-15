package com.deok9labs.aster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransformationApiIntegrationTest {

    private static final String PATH = "/api/v1/transformations";

    @Value("${local.server.port}")
    private int port;

    @Test
    void doublesNumberAndReversesText() throws IOException, InterruptedException {
        HttpResponse<String> response = TestHttpClient.postJson(port, PATH, """
                {"number": 21, "text": "hello"}
                """);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"number\":42"));
        assertTrue(response.body().contains("\"text\":\"olleh\""));
    }

    @Test
    void returnsDoubledNumberBeyondIntRange() throws IOException, InterruptedException {
        HttpResponse<String> response = TestHttpClient.postJson(port, PATH, """
                {"number": 2147483647, "text": ""}
                """);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"number\":4294967294"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"text\": \"hello\"}",
            "{\"number\": 21}",
            "{\"number\": null, \"text\": \"hello\"}",
            "{\"number\": 2147483648, \"text\": \"hello\"}",
            "{\"number\": 21.5, \"text\": \"hello\"}",
            "not json",
    })
    void rejectsInvalidRequest(String body) throws IOException, InterruptedException {
        assertEquals(400, TestHttpClient.postJson(port, PATH, body).statusCode());
    }

    @Test
    void publishesEndpointInOpenApiDocument() throws IOException, InterruptedException {
        HttpResponse<String> response = TestHttpClient.get(port, "/v3/api-docs");

        assertTrue(response.body().contains("\"" + PATH + "\""));
    }
}
