package com.nandaleio;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import org.altcha.altcha.Altcha.Challenge;
import org.altcha.altcha.Altcha.ChallengeOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nandaleio.altcha.properties.AltchaSpringBootProperties;
import com.nandaleio.altcha.services.AltchaService;

@ExtendWith(MockitoExtension.class)
class AltchaServiceTest {

    @Mock
    private AltchaSpringBootProperties properties;

    private AltchaService altchaService;

    @BeforeEach
    void setUp() {
        altchaService = new AltchaService(properties);
    }

    @Test
    void testCreateChallengeOption() {
        // Given
        when(properties.getMaxNumber()).thenReturn(100000L);
        when(properties.getHmacKey()).thenReturn("test-hmac-key");
        when(properties.getExpirationInSeconds()).thenReturn(1200L);

        // When
        ChallengeOptions options = altchaService.createChallengeOption();

        // Then
        assertNotNull(options);
        verify(properties).getMaxNumber();
        verify(properties).getHmacKey();
        verify(properties).getExpirationInSeconds();
    }

    @Test
    void testCreateChallenge() throws Exception {
        // Given
        when(properties.getMaxNumber()).thenReturn(100000L);
        when(properties.getHmacKey()).thenReturn("test-hmac-key");
        when(properties.getExpirationInSeconds()).thenReturn(1200L);

        // When
        Challenge challenge = altchaService.createChallenge();

        // Then
        assertNotNull(challenge);
        assertNotNull(challenge.challenge);
        assertNotNull(challenge.salt);
        assertNotNull(challenge.signature);
    }
    
}
