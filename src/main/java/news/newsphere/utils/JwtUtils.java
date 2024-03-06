package news.newsphere.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    @Value("${jwt.secretKey}") //application.properties에 저장되어 있는 값을 가져온다.
    private String secretKey;
    @Value("${jwt.expiredMs}0") //application.properties에 저장되어 있는 값을 가져온다
    private Long expiredMs;

    public String generateToken(Long userId){
        return JWT.create()
            .withSubject(String.valueOf(userId))
            .withExpiresAt(new Date(System.currentTimeMillis()+expiredMs))
            .sign(Algorithm.HMAC512(secretKey));
    }

    public boolean validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secretKey)).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token){
        DecodedJWT decoded = JWT.decode(token);
        String userIdStr = decoded.getSubject();
        //Long으로 형변환
        return Long.parseLong(userIdStr);
    }
}
