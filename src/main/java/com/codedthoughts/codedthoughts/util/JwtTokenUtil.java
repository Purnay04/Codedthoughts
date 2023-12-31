package com.codedthoughts.codedthoughts.util;

import com.codedthoughts.codedthoughts.services.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    private final UserService userService;
    public static final String TOKEN_PREFIX = "Bearer ";

    @Value("${app.jwt-secret}")
    private String SECRET;

    @Value("${app.jwt-expiration-milliseconds}")
    private long JWT_EXPIRATION_MILLIS;

    public String generateJwtToken(Authentication authentication){
        String userName = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + JWT_EXPIRATION_MILLIS);

        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(SECRET)
        );
    }

    public Optional<String> getUserName(String token) {
        return Optional.of(extractClaim(token, Claims::getSubject));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token).orElseThrow();
        return claimResolver.apply(claims);
    }

    public Optional<Claims> extractAllClaims(String token) {
        try {
            return Optional.of(Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody());
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException e) {
            System.out.printf("Invalid JWT token: {%s}%n", e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.printf("JWT token is expired: {%s}%n", e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.printf("JWT token is unsupported: {%s}%n", e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.printf("JWT claims string is empty: {%s}%n", e.getMessage());
        }
        return false;
    }
}
