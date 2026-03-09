package wit.backendcooker.Utils;

/*
 * @author ：jee
 * @date ：2026/3/815:25
 * @version: 1.0
 --------------------------
 */

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

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
}
