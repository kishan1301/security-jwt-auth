package ic.auth.utils;

import ic.auth.dto.UserInfo;
import ic.auth.entity.User;

import java.util.Base64;

public class Utils {
    private static final String BASIC_AUTH_PREFIX = "Basic ";
    private Utils() {}

    public static UserInfo toExternalisedUser(User user) {
        return UserInfo.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public static UserInfo parseHeader(String authHeader) {
        String decodeEmailPass = new String(Base64.getDecoder()
                .decode(authHeader.substring(BASIC_AUTH_PREFIX.length())));
        String[] emailPassPair = decodeEmailPass.split(":");
        return UserInfo.builder()
                .email(emailPassPair[0])
                .password(emailPassPair[1])
                .build();
    }

    public static User toUser(UserInfo userInfo) {
        return User.builder()
                .email(userInfo.getEmail())
                .password(userInfo.getPassword())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .build();
    }
}
