package com.spades.spades.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GetAuthenticationService {
    // Example code from: https://www.baeldung.com/get-user-in-spring-security
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
