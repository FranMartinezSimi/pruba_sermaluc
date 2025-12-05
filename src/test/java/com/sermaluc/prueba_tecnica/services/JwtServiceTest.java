package com.sermaluc.prueba_tecnica.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private final String testSecret = "mySecretKeyForTestingPurposesOnly123456789012345678901234567890";
    private final Long testExpiration = 3600000L;
    private UUID testUserId;
    private String testEmail;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", testSecret);
        ReflectionTestUtils.setField(jwtService, "expiration", testExpiration);

        testUserId = UUID.randomUUID();
        testEmail = "test@example.com";
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(testUserId, testEmail);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = jwtService.generateToken(testUserId, testEmail);

        boolean isValid = jwtService.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtService.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void testValidateToken_ExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);
        String expiredToken = jwtService.generateToken(testUserId, testEmail);

        ReflectionTestUtils.setField(jwtService, "expiration", testExpiration);

        boolean isValid = jwtService.validateToken(expiredToken);

        assertFalse(isValid);
    }

    @Test
    void testGetUserIdFromToken() {
        String token = jwtService.generateToken(testUserId, testEmail);

        String extractedUserId = jwtService.getUserIdFromToken(token);

        assertNotNull(extractedUserId);
        assertEquals(testUserId.toString(), extractedUserId);
    }

    @Test
    void testGetUserIdFromToken_InvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThrows(Exception.class, () -> {
            jwtService.getUserIdFromToken(invalidToken);
        });
    }

    @Test
    void testTokenContainsEmail() {
        String token = jwtService.generateToken(testUserId, testEmail);

        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
        String extractedEmail = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);

        assertNotNull(extractedEmail);
        assertEquals(testEmail, extractedEmail);
    }

    @Test
    void testMultipleTokensAreDifferent() {
        String token1 = jwtService.generateToken(testUserId, testEmail);

        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String token2 = jwtService.generateToken(testUserId, testEmail);

        assertNotEquals(token1, token2);
    }

    @Test
    void testTokensForDifferentUsersAreDifferent() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        String token1 = jwtService.generateToken(userId1, "user1@example.com");
        String token2 = jwtService.generateToken(userId2, "user2@example.com");

        assertNotEquals(token1, token2);

        String extractedUserId1 = jwtService.getUserIdFromToken(token1);
        String extractedUserId2 = jwtService.getUserIdFromToken(token2);

        assertEquals(userId1.toString(), extractedUserId1);
        assertEquals(userId2.toString(), extractedUserId2);
    }
}
