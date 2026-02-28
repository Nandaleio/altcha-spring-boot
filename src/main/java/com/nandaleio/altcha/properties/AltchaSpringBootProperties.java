package com.nandaleio.altcha.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "altcha-spring-boot")
public class AltchaSpringBootProperties {

    String hmacKey;

    Long maxNumber = 100000L;
    Long expirationInSeconds = 1200L;
    String apiEndpoint = "/api/captcha";
    
    // Getters and Setters :

    public String getHmacKey() {
        return hmacKey;
    }
    public void setHmacKey(String hmacKey) {
        this.hmacKey = hmacKey;
    }

    public Long getMaxNumber() {
        return maxNumber;
    }
    public void setMaxNumber(Long maxNumber) {
        this.maxNumber = maxNumber;
    }

    public Long getExpirationInSeconds() {
        return expirationInSeconds;
    }
    public void setExpirationInSeconds(Long expirationInSeconds) {
        this.expirationInSeconds = expirationInSeconds;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }
    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }
}
