package ic.auth.service.impl;

import ic.auth.entity.User;
import ic.auth.service.TokenService;
import ic.auth.service.UserService;
import ic.auth.utils.JwtUtils;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class TokenServiceImpl implements TokenService {

    private final UserService userService;

    public TokenServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void invalidateToken(String bearerToken) {
        String emailId = JwtUtils.validateAndGetSubject(bearerToken);
        User user = userService.getUserByEmail(emailId);
        user.setSessionInvalidatesAt(OffsetDateTime.MIN);
        userService.updateUser(user);
    }
}
