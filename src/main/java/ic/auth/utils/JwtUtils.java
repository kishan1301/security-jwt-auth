package ic.auth.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import ic.auth.entity.User;
import ic.auth.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

@Slf4j
public class JwtUtils {

    private JwtUtils() {
    }

    private static final String SECRET_KEY = "c3RvcnlvZmZpcmVwbGFjZWNyb3Nzd2hlcmVlYXRlbnRlbm1lbnRhbHJhYmJpdHBvcHVsYXJncmVhdGVyb3JpZ2luZGlzZWFzZXNlZWRjb21lYW5ncnlmaW5hbGx5bG9zZWdvdmVybm1lbnRyYWlscm9hZGRldGFpbGJyb3VnaHRoZWxkZW5lbXljYWxsbG9jYXRpb25raW5kbGF3b2ZmaWNlcnNwZWNpZXNsYXJnZXJ0aHVzbWFpbnBpbGVwYXJhZ3JhcGhoaWdoZXJyaHltZW93bmVsZWN0cmljY29hY2hoZWxkdHJlZXNhaWR1cG9uZmVldGJlZm9yZWFybXlkaQ==";
    private static final String ISSUER = "token-issuer";

    public static String generateToken(User user) {
        return JWT.create()
                .withExpiresAt(user.getSessionInvalidatesAt().toInstant().minus(1, ChronoUnit.MINUTES))
                .withExpiresAt(user.getSessionInvalidatesAt().toInstant())
                .withSubject(user.getEmail())
                .withIssuer(ISSUER)
                .withClaim("first_name", user.getFirstName())
                .withClaim("last_name", user.getLastName())
                .sign(getAlgorithm());
    }

    public static String validateAndGetSubject(String bearerToken) {
        DecodedJWT decodedJWT = JWT.decode(bearerToken.substring("Bearer ".length()));
        verifyExpiry(decodedJWT);
        verifySign(decodedJWT);
        return decodedJWT.getSubject();
    }

    private static void verifySign(DecodedJWT decodedJWT) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
        try {
            verifier.verify(decodedJWT);
        } catch (JWTVerificationException e) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    private static void verifyExpiry(DecodedJWT decodedJWT) {
        if (Instant.now().isAfter(decodedJWT.getExpiresAtAsInstant()))
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, "Security token has been expired!");
    }

    public static Date extractExpiration(String bearerToken) {
        return extractClaim(bearerToken, Claims::getExpiration);
    }

    private static <T> T extractClaim(String bearerToken, Function<Claims, T> claimResolver) {
        return null;
    }

    private static Algorithm getAlgorithm() {
        return Algorithm.HMAC256(SECRET_KEY);
    }
}
