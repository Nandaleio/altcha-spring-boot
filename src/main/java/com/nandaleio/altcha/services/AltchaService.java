package com.nandaleio.altcha.services;


import org.altcha.altcha.Altcha;
import org.altcha.altcha.Altcha.Challenge;
import org.altcha.altcha.Altcha.ChallengeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.nandaleio.altcha.properties.AltchaSpringBootProperties;

import jakarta.annotation.PostConstruct;

@Service
public class AltchaService {

    private final Logger log = LoggerFactory.getLogger(AltchaService.class);
    private AltchaSpringBootProperties properties;

    public AltchaService(AltchaSpringBootProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void init() {
        log.debug("AltchaService bean created");
    }

    public ChallengeOptions createChallengeOption() {
        return new ChallengeOptions()
                .setMaxNumber(this.properties.getMaxNumber()) // the maximum random number
                .setHmacKey(this.properties.getHmacKey())
                .setExpiresInSeconds(this.properties.getExpirationInSeconds()); // 1 hour expiration
    }

    public Challenge createChallenge() throws Exception {
        ChallengeOptions options = createChallengeOption();
        log.trace("Creating the challenge with options: {}, {}", options.maxNumber, options.expires);
        return Altcha.createChallenge(options);
    }

    public boolean validateResponse(String payload) throws Exception {
            log.trace("verifying the payload");
            return Altcha.verifySolution(payload, this.properties.getHmacKey(), true);
    }
}
