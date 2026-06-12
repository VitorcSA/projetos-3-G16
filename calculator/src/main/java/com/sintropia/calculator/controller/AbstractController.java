package com.sintropia.calculator.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.HttpServletResponse;

public abstract class AbstractController {
    protected void addCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Strict")
                .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
