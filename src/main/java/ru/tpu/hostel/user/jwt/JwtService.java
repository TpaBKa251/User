package ru.tpu.hostel.user.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.internal.exception.ServiceException;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.repository.UserRepository;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String USER_ID = "userId";

    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.accessTokenLifetime}")
    private Duration accessTokenExpiration;

    @Value("${jwt.refreshTokenLifetime}")
    private Duration refreshTokenExpiration;

    private Key getSigningKey() {
        return new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim(USER_ID, user.getId().toString())
                .claim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(role -> role.replace("ROLE_", ""))
                        .toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration.toMillis()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim(USER_ID, user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration.toMillis()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return UUID.fromString(claims.get(USER_ID, String.class));
    }

    public void checkRefreshTokenValidity(String token) {
        if (token == null || token.isBlank()) {
            throw new ServiceException.Unauthorized("Token is empty");
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            UUID userId = UUID.fromString(claims.get(USER_ID, String.class));
            if (!userId.equals(getUserIdFromToken(token))) {
                throw new ServiceException.Unauthorized("Invalid token: userId is missing");
            }

            if (!userRepository.existsById(userId)) {
                throw new ServiceException.Unauthorized("User not found");
            }
        } catch (ServiceException.Unauthorized e) {
            throw e;
        } catch (ExpiredJwtException e) {
            throw new ServiceException.Unauthorized("Token is expired");
        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException e) {
            throw new ServiceException.Unauthorized("Invalid token");
        } catch (Exception e) {
            throw new ServiceException.InternalServerError("Error processing JWT", e);
        }
    }

}

