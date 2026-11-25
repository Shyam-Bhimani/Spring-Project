package com.spring.boot.demo.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;
    private final String issuer;

    public JwtService(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration-ms}") long expirationMs,
        @Value("${jwt.issuer}") String issuer){
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs=expirationMs;
        this.issuer=issuer;
    }

    public String generateToken(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date issueAt = new Date();

        Date expiration = new Date(issueAt.getTime()+expirationMs);

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuer(issuer)
                .issuedAt(issueAt)
                .expiration(expiration)
                .claim("roles",roles)
                .signWith(secretKey,Jwts.SIG.HS256)
                .compact();
    }
}
