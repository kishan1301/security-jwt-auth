package ic.auth.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import ic.auth.TokenType;
import ic.auth.constants.CommonConstants;
import ic.auth.entity.User;
import ic.auth.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.BiFunction;

import static ic.auth.constants.CommonConstants.TYP_CLAIM;

@Slf4j
public class JwtUtils {

    private JwtUtils() {
    }

    private static final BiFunction<DecodedJWT, TokenType, Void> tokenTypCheck = (decodedJWT, tokenType) -> {
        if (!tokenType.name().equals(decodedJWT.getClaim(TYP_CLAIM).asString())) {
            throw new UnauthorizedException(HttpStatus.BAD_REQUEST,
                    String.format("Incorrect token type is provided, required %s but provided %s", tokenType.name(), decodedJWT.getClaim(TYP_CLAIM).asString()));
        }
        return null;
    };

    private static final BiFunction<DecodedJWT, TokenType, Void> tokenExpiryCheck = (decodedJWT, tokeType) -> {
        if (Instant.now().isAfter(decodedJWT.getExpiresAtAsInstant())) {
            String message = "Security(%s) token has been expired%s!";
            message = tokeType == TokenType.REFRESH_TOKEN
                    ? String.format(message, tokeType.name(), ", please log-in again")
                    : String.format(message, tokeType.name(), CommonConstants.EMPTY);
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, message);
        }
        return null;
    };

    private static void verifySign(DecodedJWT decodedJWT, String secretKey, String issuer) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build();
        try {
            verifier.verify(decodedJWT);
        } catch (JWTVerificationException e) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    public static String generateToken(User user, Instant now, String secretString, String issuer) {
        JWTCreator.Builder jwtBuilder = JWT.create()
                .withExpiresAt(user.getSessionInvalidatesAt().toInstant());
        return generateToken(user, jwtBuilder, Map.of(TYP_CLAIM, TokenType.ACCESS_TOKEN.name()),
                secretString, issuer, now);
    }

    public static String generateRefreshToken(User user, Instant now,
                                              String secretString, String issuer) {
        JWTCreator.Builder jwtBuilder = JWT.create()
                .withExpiresAt(user.getRefreshValidTill().toInstant());
        return generateToken(user, jwtBuilder, Map.of(TYP_CLAIM, TokenType.REFRESH_TOKEN.name()),
                secretString, issuer, now);
    }

    private static String generateToken(User user, JWTCreator.Builder jwtBuilder, Map<String, String> claims,
                                        String secretString, String issuer, Instant now) {
        jwtBuilder
                .withIssuedAt(now)
                .withSubject(user.getEmail())
                .withIssuer(issuer)
                .withClaim("first_name", user.getFirstName())
                .withClaim("last_name", user.getLastName());
        claims.forEach(jwtBuilder::withClaim);
        return jwtBuilder.sign(getAlgorithm(secretString));
    }

    public static String validateAndGetSubject(String bearerToken, TokenType tokenType, String secretString, String issuer) {
        DecodedJWT decodedJWT = validateAndGetDecodedJwt(bearerToken, tokenType, secretString, issuer);
        return decodedJWT.getSubject();
    }

    private static DecodedJWT validateAndGetDecodedJwt(String bearerToken, TokenType tokenType,
                                                       String secretString, String issuer) {
        DecodedJWT decodedJWT = JWT.decode(bearerToken.substring("Bearer ".length()));
        tokenTypCheck.apply(decodedJWT, tokenType);
        tokenExpiryCheck.apply(decodedJWT, tokenType);
        verifySign(decodedJWT, secretString, issuer);
        return decodedJWT;
    }

    public static Date extractExpiration(String bearerToken) {
        DecodedJWT decodedJWT = JWT.decode(bearerToken.substring("Bearer ".length()));
        return decodedJWT.getExpiresAt();
    }

    private static Algorithm getAlgorithm(String secretKey) {
        return Algorithm.HMAC256(secretKey);
    }
}