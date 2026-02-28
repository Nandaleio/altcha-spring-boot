package com.nandaleio.altcha.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nandaleio.altcha.annotations.RequireAltcha;
import com.nandaleio.altcha.exceptions.AltchaPropertyMissingException;
import com.nandaleio.altcha.services.AltchaService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AltchaFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(AltchaFilter.class);

    private static final String PROPERTY_NAME = "altcha"; 
    
    private final HandlerMappingIntrospector introspector;
    private final AltchaService service;
    
    public AltchaFilter(AltchaService service, HandlerMappingIntrospector introspector) {
        this.service = service;
        this.introspector = introspector;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HandlerMethod hm = this.findHandlerMethod(request);

        // if we can't find the right controller endpoint (method) let Spring handle it
        if (hm == null) {
            filterChain.doFilter(request, response);
            return;
        }
    
        RequireAltcha annoCaptcha = hm.getMethodAnnotation(RequireAltcha.class);
        RequireAltcha annoCaptchaClass = hm.getBeanType().getAnnotation(RequireAltcha.class);

        // if there are no @RequireCaptcha annotation then no need to check captcha
        if(annoCaptcha == null && annoCaptchaClass == null) {
            log.info("Not a captcha request");
            filterChain.doFilter(request, response);
            return;
        }

        CachedRequestHttpServletRequest wrappedRequest = new CachedRequestHttpServletRequest(request);

        // Get altcha from request parameter (works for form data and query params)
        String payload = wrappedRequest.getParameter(PROPERTY_NAME);
        if(payload == null) {
            //if not in form or query then get the payload from the json body
            payload = extractPayloadFromJson(wrappedRequest);
        }
        log.info("payload: {}" , payload);

        //TODO get the altche field form more request type

        if(payload == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            boolean isValid = this.service.validateResponse(payload);
            log.info("captcha valid: {}" , isValid);

            if(isValid) {
                filterChain.doFilter(wrappedRequest, response);
                return;
            }
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private String extractPayloadFromJson(CachedRequestHttpServletRequest request) {
            try {
                // get altcha from json
                StringBuilder buffer = new StringBuilder();
                String line;
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String body = buffer.toString();
                ObjectMapper mapper = new ObjectMapper();
                // Convert JSON string to Map
                Map<String, String> map = mapper.readValue(body, new TypeReference<Map<String, String>>() {});
                return map.get(PROPERTY_NAME);
            } catch (Exception e) {
                throw new AltchaPropertyMissingException();
            }
    }

    private HandlerMethod findHandlerMethod(HttpServletRequest request) {
        for (HandlerMapping mapping : this.introspector.getHandlerMappings()) {
            try {

                HandlerExecutionChain h = mapping.getHandler(request);
                if(h == null) continue;

                Object handler = h.getHandler();
                if (handler instanceof HandlerMethod) {
                    return (HandlerMethod) handler;
                }
            } catch (Exception ignore) {
                // handler not found in this mapping
            }
        }
        return null;
    }





    private static class CachedRequestHttpServletRequest extends HttpServletRequestWrapper {

        private byte[] cachedBody;

        public CachedRequestHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            this.cachedBody = StreamUtils.copyToByteArray(request.getInputStream());
        }

        @Override
        public ServletInputStream getInputStream() {
            return new CachedRequestServletInputStream(this.cachedBody);
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.cachedBody)));
        }
    }

    private static class CachedRequestServletInputStream extends ServletInputStream {

        private InputStream cachedBodyInputStream;

        public CachedRequestServletInputStream(byte[] cachedBody) {
            this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished() {
            try {
                return cachedBodyInputStream.available() == 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return cachedBodyInputStream.read();
        }
    }


}
