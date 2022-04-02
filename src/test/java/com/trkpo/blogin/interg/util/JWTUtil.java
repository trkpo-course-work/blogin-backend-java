package com.trkpo.blogin.interg.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;

public class JWTUtil {
    private String secret = "8Zz5tw0Ionm3XPZZfN0NOml3z9FMfmpgXwovR9fp6ryDIoGRM8EPHAB6iHsc0fb";
    public String createToken(String login) {
        Claims claims = Jwts.claims().setSubject(login);
        return Jwts.builder()
                .setClaims(claims)
                .signWith(new SecretKeySpec(secret.getBytes(), 0, secret.length(), "HmacSHA256"), SignatureAlgorithm.HS256)
                .compact();
    }
}
