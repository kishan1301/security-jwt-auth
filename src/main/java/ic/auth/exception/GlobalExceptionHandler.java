package ic.auth.exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String ERROR_OCCURRED = "Error occurred!";

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Error> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error(ERROR_OCCURRED, ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Error(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(Exception ex) {
        log.error(ERROR_OCCURRED, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Error> handleUnauthorizedException(UnauthorizedException ex) {
        log.error(ERROR_OCCURRED, ex);
        return ResponseEntity.status(ex.getHttpStatus())
                .body(new Error(ex.getHttpStatus().value(), ex.getMessage()));
    }

    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity<Error> handleJWTDecodeException(JWTDecodeException ex) {
        log.error(ERROR_OCCURRED, ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }
}