package com.application.HotelBooking.utils;

//import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTUtils {

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 *7;

    private final SecretKey key;

    JWTUtils(@Value("${secretkey}") String secretstring){
//        Dotenv dotenv = Dotenv.load();
//        String secretstring = dotenv.get("secretkey");
//        System.out.println("The secret key is "+secretstring);
        byte[] keybytes = Base64.getDecoder().decode(secretstring.getBytes(StandardCharsets.UTF_8));
        this.key = new SecretKeySpec(keybytes , "HmacSHA256");
    }

    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String extractUserName(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace(); // Log signature errors
            throw e;
        }
    }


    public boolean isValidToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
//        System.out.println("UserDetails name "+userDetails.getUsername());
//        System.out.println("UserName Extracted "+userName);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        boolean ans = extractExpiration(token).before(new Date());
//        System.out.println("Token Expired "+ans);
        return ans;
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}