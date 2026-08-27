package com.nexaworkspace.saas.security;

import com.nexaworkspace.saas.user.UserAccount;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final Duration ttl;
    public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.ttl-minutes:120}") long minutes){
        this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.ttl=Duration.ofMinutes(minutes);
    }
    public String issue(UserAccount user){
        var now=Instant.now();
        return Jwts.builder().subject(user.getEmail()).claim("uid", user.getId().toString()).claim("tid", user.getTenant().getId().toString())
                .claim("name", user.getName()).claim("role", user.getRole().name()).issuedAt(Date.from(now)).expiration(Date.from(now.plus(ttl))).signWith(key).compact();
    }
    public Claims parse(String token){ return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); }
}
