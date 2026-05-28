package com.sintropia.calculator.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService{

	@Value("${jwt.secret}")
	private String secret;
	
	public String generateToken(String email){
		return Jwts.builder()
			.subject(email)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
			.signWith(Keys.hmacShaKeyFor(secret.getBytes()))
			.compact();
	}

	public String extractEmail(String token) {
		return Jwts.parser()
			.verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getSubject();
	}
}