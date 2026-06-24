package com.mybatis_crud.board.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiration = expiration;
    }

    public String generateToken(String id) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder().subject(id).issuedAt(now).expiration(expiry).signWith(key).compact();
    }

    public String extractId(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try{
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
            return true;
        } catch (Exception e){
            System.out.println(e.getClass().getSimpleName());
            return false;
        }
    }

}
