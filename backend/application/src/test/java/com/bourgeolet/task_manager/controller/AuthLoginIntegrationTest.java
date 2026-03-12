package com.bourgeolet.task_manager.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.datasource.url=jdbc:h2:mem:auth-login-it;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.admin.initial-password=integration-admin-password",
        "app.auth.jwt.secret=integration-jwt-secret-key-2026-minimum-32-bytes",
        "app.auth.jwt.ttl=PT20M"
    }
)
@ActiveProfiles("dev")
class AuthLoginIntegrationTest {

    private static final String JWT_SECRET = "integration-jwt-secret-key-2026-minimum-32-bytes";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Test
    void login_whenCredentialsAreValid_shouldReturnJwtToken() throws IOException, InterruptedException {
        HttpResponse<String> response = postJson(
            "/auth/login",
            """
                {
                  "username": "admin",
                  "password": "integration-admin-password"
                }
                """
        );

        assertEquals(200, response.statusCode());

        JsonNode responseBody = OBJECT_MAPPER.readTree(response.body());
        String token = responseBody.path("token").asText();
        String type = responseBody.path("type").asText();
        OffsetDateTime expiration = OffsetDateTime.parse(responseBody.path("expiration").asText());

        assertThat(token).isNotBlank();
        assertThat(type).isEqualTo("Bearer");
        assertThat(expiration).isAfter(OffsetDateTime.now().minusMinutes(1));

        Claims claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8)))
            .build()
            .parseClaimsJws(token)
            .getBody();

        assertThat(claims.getSubject()).isEqualTo("admin");
        assertThat(claims.get("roles", List.class)).contains("ADMIN");
        assertThat(claims.getExpiration().toInstant()).isEqualTo(expiration.toInstant());
    }

    @Test
    void login_whenCredentialsAreInvalid_shouldReturnUnauthorizedWithoutInformationLeak() throws IOException, InterruptedException {
        HttpResponse<String> response = postJson(
            "/auth/login",
            """
                {
                  "username": "admin",
                  "password": "wrong-password"
                }
                """
        );

        assertEquals(401, response.statusCode());
        assertThat(response.body()).isBlank();
    }

    private HttpResponse<String> postJson(String path, String body) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + path))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}


