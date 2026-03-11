package com.bourgeolet.task_manager.service;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    private static final String TEST_SECRET = "dGFza01hbmFnZXJTZWNyZXRLZXlGb3JKV1RBdXRoZW50aWNhdGlvbjIwMjQ=";
    private static final long ACCESS_EXPIRATION = 3600000L;
    private static final long REFRESH_EXPIRATION = 604800000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", ACCESS_EXPIRATION);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", REFRESH_EXPIRATION);
    }

    private UserDetails buildUser(String username) {
        return User.builder()
                .username(username)
                .password("encodedPassword")
                .roles("USER")
                .build();
    }

    @Test
    void generateAccessToken_whenValidUser_shouldReturnNonNullToken() {
        UserDetails userDetails = buildUser("antoine");

        String token = jwtService.generateAccessToken(userDetails);

        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void generateRefreshToken_whenValidUser_shouldReturnNonNullToken() {
        UserDetails userDetails = buildUser("antoine");

        String token = jwtService.generateRefreshToken(userDetails);

        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void extractUsername_whenValidToken_shouldReturnCorrectUsername() {
        UserDetails userDetails = buildUser("antoine");
        String token = jwtService.generateAccessToken(userDetails);

        String extracted = jwtService.extractUsername(token);

        assertThat(extracted).isEqualTo("antoine");
    }

    @Test
    void isTokenValid_whenTokenMatchesUser_shouldReturnTrue() {
        UserDetails userDetails = buildUser("antoine");
        String token = jwtService.generateAccessToken(userDetails);

        boolean valid = jwtService.isTokenValid(token, userDetails);

        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValid_whenTokenBelongsToOtherUser_shouldReturnFalse() {
        UserDetails owner = buildUser("antoine");
        UserDetails other = buildUser("other");
        String token = jwtService.generateAccessToken(owner);

        boolean valid = jwtService.isTokenValid(token, other);

        assertThat(valid).isFalse();
    }

    @Test
    void isTokenValid_whenTokenIsExpired_shouldReturnFalse() {
        JwtService shortLivedService = new JwtService();
        ReflectionTestUtils.setField(shortLivedService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(shortLivedService, "accessTokenExpiration", -1000L);
        ReflectionTestUtils.setField(shortLivedService, "refreshTokenExpiration", REFRESH_EXPIRATION);

        UserDetails userDetails = buildUser("antoine");
        String token = shortLivedService.generateAccessToken(userDetails);

        assertThatThrownBy(() -> shortLivedService.isTokenValid(token, userDetails))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void accessToken_andRefreshToken_shouldBeDifferent() {
        UserDetails userDetails = buildUser("antoine");

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        assertThat(accessToken).isNotEqualTo(refreshToken);
    }
}
