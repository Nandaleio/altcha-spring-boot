package com.nandaleio.altcha.services;

import org.altcha.altcha.Altcha;
import org.altcha.altcha.Altcha.Challenge;
import org.altcha.altcha.Altcha.ChallengeOptions;
import org.springframework.stereotype.Service;

import com.nandaleio.altcha.properties.AltchaSpringBootProperties;

@Service
public class AltchaService {
    private AltchaSpringBootProperties properties;

    public AltchaService(AltchaSpringBootProperties properties) {
        this.properties = properties;
    }

    public ChallengeOptions createChallengeOption() {
        return new ChallengeOptions()
                .setMaxNumber(this.properties.getMaxNumber()) // the maximum random number
                .setHmacKey(this.properties.getHmacKey())
                .setExpiresInSeconds(this.properties.getExpirationInSeconds()); // 1 hour expiration
    }

    public Challenge createChallenge() throws Exception {
        return Altcha.createChallenge(createChallengeOption());
    }

    public boolean validateResponse(String payload) throws Exception {
            // Verify the solution
            return Altcha.verifySolution(payload, this.properties.getHmacKey(), true);
    }
}
