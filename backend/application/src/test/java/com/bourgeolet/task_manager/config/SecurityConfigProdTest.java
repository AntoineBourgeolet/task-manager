package com.bourgeolet.task_manager.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("prod")
class SecurityConfigProdTest {

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
    void anonymousRequestOnProtectedEndpointReturns401() throws IOException, InterruptedException {
        assertEquals(UNAUTHORIZED.value(), getStatus("/account"));
    }

    @Test
    void swaggerIsNotPublicInProd() throws IOException, InterruptedException {
        assertEquals(UNAUTHORIZED.value(), getStatus("/swagger-ui.html"));
    }

    @Test
    void h2ConsoleIsNotPublicInProd() throws IOException, InterruptedException {
        assertEquals(UNAUTHORIZED.value(), getStatus("/h2-console/"));
    }
}



