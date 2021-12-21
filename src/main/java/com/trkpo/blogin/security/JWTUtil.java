package com.trkpo.blogin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.function.Function;

@Component
public class JWTUtil {
    private static JWTConfiguration jwtConfiguration;

    @Autowired
    private JWTUtil(JWTConfiguration jwtConfiguration) {
        JWTUtil.jwtConfiguration = jwtConfiguration;
    }

    public static String extractId(String token){
        return extractClaim(token, Claims::getSubject);
    }
    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private static Claims extractAllClaims(String token){
        SecretKey secretKey = new SecretKeySpec(jwtConfiguration.getKey().getBytes(), 0, jwtConfiguration.getKey().length(), "HmacSHA256");
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
}
