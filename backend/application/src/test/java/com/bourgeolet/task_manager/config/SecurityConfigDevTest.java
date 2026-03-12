package com.bourgeolet.task_manager.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class SecurityConfigDevTest {

    @LocalServerPort
    private int port;

    private int getStatus(String path) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + path))
            .GET()
            .build();
        return client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

    @Test
    void swaggerIsAccessibleWithoutAuthenticationInDev() throws IOException, InterruptedException {
        assertNotEquals(HttpStatus.UNAUTHORIZED.value(), getStatus("/swagger-ui.html"));
    }

    @Test
    void h2ConsoleIsAccessibleWithoutAuthenticationInDev() throws IOException, InterruptedException {
        assertNotEquals(HttpStatus.UNAUTHORIZED.value(), getStatus("/h2-console/"));
    }
}


