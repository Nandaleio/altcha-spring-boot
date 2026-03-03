package com.nandaleio.altcha.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.nandaleio.altcha.annotations.RequireAltcha;
import com.nandaleio.altcha.services.AltchaService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AltchaInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(AltchaInterceptor.class);
    private static final String PROPERTY_NAME = "altcha"; 

    private final AltchaService service;
    
    public AltchaInterceptor(AltchaService service) {
        this.service = service;
    }


    @PostConstruct
    void init() {
        log.debug("AltchaFilter bean created");
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) return true;
        HandlerMethod hm = (HandlerMethod) handler;
    
        RequireAltcha annoCaptcha = hm.getMethodAnnotation(RequireAltcha.class);
        RequireAltcha annoCaptchaClass = hm.getBeanType().getAnnotation(RequireAltcha.class);

        // if there are no @RequireCaptcha annotation then no need to check captcha
        if(annoCaptcha == null && annoCaptchaClass == null) {
            log.debug("Not a captcha request");
            return true;
        }

        try {
            // Get altcha from request parameter (works for form data and query params)
            String payload = request.getParameter(PROPERTY_NAME);
            if(payload == null) {
                //if not in form or query then get the payload from the request header
                payload = request.getHeader("ALTCHA-PAYLOAD");
            }
            log.debug("payload: {}" , payload);

            //TODO get the altcha field form more request type (eg: json body)

            if(payload == null) {
                log.warn("Error while getting the resolved challenge");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return false;
            }

            boolean isValid = this.service.validateResponse(payload);

            if(isValid) {
                log.info("captcha valid !");
                return true;
            }

            log.error("CAPTCHA INVALID for request {} !", request.getPathInfo());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } catch (Exception e) {
            log.error("Error while checking the captcha", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        return false;
    }

}
