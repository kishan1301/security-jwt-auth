package ic.auth.constants;

import ic.auth.entity.User;
import ic.auth.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface CommonConstants {
    String TYP_CLAIM = "typ";
    String REFRESH_IAT = "iat";
    String REFRESH_EXP = "exp";

    BiFunction<User, Date, Void> validateSessionExpiry = (user, date) -> {
        if (user.getSessionInvalidatesAt() == null
                || user.getSessionInvalidatesAt().toEpochSecond() * 1000 != date.toInstant().toEpochMilli()) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, "Security token is invalid!");
        }
        return null;
    };

    Function<User, Void> validateRefreshTokenExpiry = user -> {
        if (user.getRefreshValidTill() == null || Instant.now().isAfter(user.getRefreshValidTill().toInstant())) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, "Refresh token has expired, please login again!");
        }
        return null;
    };
    Object EMPTY = "";
}