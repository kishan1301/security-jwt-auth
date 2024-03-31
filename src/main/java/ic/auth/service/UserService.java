package ic.auth.service;

import ic.auth.dto.TokenInfo;
import ic.auth.dto.UserInfo;
import ic.auth.entity.User;

public interface UserService {

    User createUser(String authHeader, UserInfo userInfo);

    User getUserByEmail(String email);

    TokenInfo getJwtToken(String authHeader);

    User updateUser(User user);

    User getUser(String bearerToken);

    User invalidateUserSession(User user);

    TokenInfo refreshToken(String emailId);
}
