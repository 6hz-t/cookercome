package wit.backendcooker.Utils;

/*
 * @author ：jee
 * @date ：2026/3/815:25
 * @version: 1.0
 --------------------------
 */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
@Slf4j
@Component
public class JwtTokenUtil {



    @Value("${jwt.secret}")
    private  String secret;

    @Value("${jwt.expiration}")
    private  long expiration;


     public  String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }


     public  boolean validateToken(String token) {
        try {

            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getBody();

            log.info("token 校验" );
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
         return Jwts.parser()
                 .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                 .build()
                 .parseSignedClaims(token)
                 .getPayload()
                 .getSubject();
    }
}
