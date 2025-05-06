package com.ticketgo.util;

import com.ticketgo.constant.RedisKeys;
import com.ticketgo.entity.User;
import com.ticketgo.exception.AppException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {

    private final RedissonClient redissonClient;

    @Value("${jwt.private.key.path}")
    private String privateKeyPath;

    @Value("${jwt.public.key.path}")
    private String publicKeyPath;

    private static final String ENCRYPT_ALGORITHM = "RSA";
    public static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";
    public static final String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";
    public static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
    public static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";
    public static final String EMPTY_STRING = "";

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 30 * 60 * 1000; // 30 minutes
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 1 day

    private PrivateKey getPrivateKey() {
        try {
            InputStream inputStream  = getClass().getClassLoader().getResourceAsStream(privateKeyPath);
            if (inputStream == null) {
                log.error("Failed to load private key: {}", privateKeyPath);
                throw new AppException("Private key file not found", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            String privateKeyPEM = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            privateKeyPEM =
                    privateKeyPEM.replace(BEGIN_PRIVATE_KEY, EMPTY_STRING)
                            .replace(END_PRIVATE_KEY, EMPTY_STRING)
                            .replaceAll("\\s+", "");

            byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key: " + e.getMessage(), e);
        }
    }

    private PublicKey getPublicKey() {
        try {
            InputStream inputStream  = getClass().getClassLoader().getResourceAsStream(publicKeyPath);
            if (inputStream == null) {
                log.error("Failed to load public key: {}", publicKeyPath);
                throw new AppException("Public key file not found", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            String publicKeyPEM = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            publicKeyPEM =
                    publicKeyPEM.replace(BEGIN_PUBLIC_KEY, EMPTY_STRING)
                            .replace(END_PUBLIC_KEY, EMPTY_STRING)
                            .replaceAll("\\s+", "");

            byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load public key: " + e.getMessage(), e);
        }
    }

    private String generateToken(User user, long expirationTime) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateAccessToken(User user) {
        return generateToken(user, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction)  {
        return claimsTFunction.apply(Jwts.parser()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody());
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private boolean isTokenInBlackList(String token) {
        String redisKey = RedisKeys.blackListTokenKey;
        RList<String> blackList = redissonClient.getList(redisKey);
        return blackList.contains(token);
    }

    public boolean isTokenValid(String token) {
        return !isTokenExpired(token) && !isTokenInBlackList(token);
    }
}