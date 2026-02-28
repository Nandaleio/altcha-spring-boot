package com.nandaleio.altcha.controller;

import org.altcha.altcha.Altcha;
import org.springframework.web.bind.annotation.RestController;

import com.nandaleio.altcha.services.AltchaService;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class AltchaController {

    private final AltchaService service;

    public AltchaController(AltchaService service) {
        this.service = service;
    }

    @GetMapping("${altcha-spring-boot.api-endpoint}")
    public Altcha.Challenge generateChallenge() throws Exception {
        return service.createChallenge();
    }
}
