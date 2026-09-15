package com.deok9labs.aster;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

final class TestHttpClient {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();

    private TestHttpClient() {
    }

    static HttpResponse<String> get(int port, String path) throws IOException, InterruptedException {
        return send(request(port, path).GET());
    }

    static HttpResponse<String> postJson(int port, String path, String json) throws IOException, InterruptedException {
        return send(request(port, path)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)));
    }

    private static HttpRequest.Builder request(int port, String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .timeout(TIMEOUT);
    }

    private static HttpResponse<String> send(HttpRequest.Builder builder) throws IOException, InterruptedException {
        return CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }
}
