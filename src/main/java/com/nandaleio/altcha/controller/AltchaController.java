package com.nandaleio.altcha.controller;


import org.altcha.altcha.Altcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import com.nandaleio.altcha.services.AltchaService;

import jakarta.annotation.PostConstruct;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class AltchaController {

    private final AltchaService service;
    private final Logger log = LoggerFactory.getLogger(AltchaController.class);

    public AltchaController(AltchaService service) {
        this.service = service;
    }

    @PostConstruct
    void init() {
        log.debug("AltchaController bean created");
    }

    @GetMapping("${altcha-spring-boot.api-endpoint:/api/captcha}")
    public Altcha.Challenge generateChallenge() throws Exception {
        return service.createChallenge();
    }
}
