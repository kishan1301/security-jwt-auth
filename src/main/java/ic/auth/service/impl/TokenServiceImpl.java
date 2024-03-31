package ic.auth.service.impl;

import ic.auth.TokenType;
import ic.auth.dto.TokenInfo;
import ic.auth.entity.User;
import ic.auth.service.TokenService;
import ic.auth.service.UserService;
import ic.auth.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    private final UserService userService;

    @Value("${auth.flow.token.access.expiry-in-seconds}")
    private Long accessTokenExpiry;
    @Value("${auth.flow.token.refresh.expiry-in-seconds}")
    private Long refreshTokenExpiry;
    @Value("${auth.flow.token.issuer}")
    private String issuer;
    @Value("${auth.flow.token.secret-string}")
    private String secretString;

    public TokenServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void invalidateToken(String bearerToken) {
        String emailId = JwtUtils.validateAndGetSubject(bearerToken, TokenType.ACCESS_TOKEN, secretString, issuer);
        User user = userService.getUserByEmail(emailId);
        userService.invalidateUserSession(user);
    }

    @Override
    public TokenInfo refreshToken(String refreshToken) {
        String emailId = JwtUtils.validateAndGetSubject(refreshToken, TokenType.REFRESH_TOKEN, secretString, issuer);
        return userService.refreshToken(emailId);
    }
}
