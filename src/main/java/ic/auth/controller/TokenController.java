package ic.auth.controller;

import ic.auth.dto.TokenInfo;
import ic.auth.service.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("token")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping(path = "invalidate")
    public ResponseEntity<TokenInfo> invalidate(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        tokenService.invalidateToken(bearerToken);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping(path = "refresh-jwt")
    public ResponseEntity<TokenInfo> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        TokenInfo tokenInfo = tokenService.refreshToken(bearerToken);
        return ResponseEntity.ok(tokenInfo);
    }
}