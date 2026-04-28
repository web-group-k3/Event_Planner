package com.k3.examen.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.sql.Date;

public class JwtUtil {
    private static  final String SECRET_KEY = System.getenv("JWT_SECRET");
    private static  final  long EXPIRATION = 1000 * 60 * 60 * 24; //24h
    private static  final Key key  = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public static String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    private static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
