package com.shop.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

@Service
public class JwtService {

  @Value("${app.jwt.secret}")
  private String secret;

  @Value("${app.jwt.expiration-minutes}")
  private long expMinutes;

  private SecretKey key() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String generate(String email) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .subject(email)
        .issuedAt(new Date(now))
        .expiration(new Date(now + expMinutes * 60_000))
        .signWith(key())
        .compact();
  }

  public String extractEmail(String token) {
    return Jwts.parser()
    		.verifyWith(key())
    		.build()
    		.parseSignedClaims(token)
    		.getPayload()
    		.getSubject();
  }
}
