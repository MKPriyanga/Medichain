package com.cts.util;
 
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
 
import javax.crypto.SecretKey;
 
@Component
public class GatewayJwtUtil {
 
    @Value("${jwt.secret}")
    private String secret;
 
    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
 
    public Claims validateAndExtract(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
 
    public boolean isValid(String token) {
        try {
            validateAndExtract(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}