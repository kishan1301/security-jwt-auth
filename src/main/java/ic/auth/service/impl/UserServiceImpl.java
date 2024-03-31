package ic.auth.service.impl;

import ic.auth.enums.TokenType;
import ic.auth.constants.CommonConstants;
import ic.auth.dto.TokenInfo;
import ic.auth.dto.UserInfo;
import ic.auth.entity.User;
import ic.auth.exception.UnauthorizedException;
import ic.auth.repo.UserRepo;
import ic.auth.service.UserService;
import ic.auth.utils.JwtUtils;
import ic.auth.utils.Utils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${auth.flow.token.access.expiry-in-seconds}")
    private Long accessTokenExpiry;
    @Value("${auth.flow.token.refresh.expiry-in-seconds}")
    private Long refreshTokenExpiry;
    @Value("${auth.flow.token.issuer}")
    private String issuer;
    @Value("${auth.flow.token.secret-string}")
    private String secretString;

    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(String authHeader, UserInfo userInfo) {
        UserInfo parsedInfo = Utils.parseHeader(authHeader);
        userInfo.setEmail(parsedInfo.getEmail());
        userInfo.setPassword(passwordEncoder.encode(parsedInfo.getPassword()));
        User user = null;
        try {
            user = getUserByEmail(userInfo.getEmail());
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
        }
        if (user != null) {
            throw new EntityExistsException("User already exist!");
        }
        user = Utils.toUser(userInfo);
        return userRepo.saveAndFlush(user);
    }

    @Override
    public User getUserByEmail(String email) {
        User exampleUser = User.builder()
                .email(email).build();
        ExampleMatcher exMatcher = ExampleMatcher.matching()
                .withIgnorePaths("id");
        Optional<User> userOpt = userRepo.findOne(Example.of(exampleUser, exMatcher));
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("User not found!");
        }
        return userOpt.get();
    }

    @Override
    public TokenInfo getJwtToken(String authHeader) {
        UserInfo parsedInfo = Utils.parseHeader(authHeader);
        User user = getUserByEmail(parsedInfo.getEmail());
        if (!passwordEncoder.matches(parsedInfo.getPassword(), user.getPassword())) {
            throw new UnauthorizedException(HttpStatus.BAD_REQUEST, "Incorrect password!");
        }
        OffsetDateTime now = OffsetDateTime.now();
        user.setSessionInvalidatesAt(now.plusSeconds(accessTokenExpiry));
        user.setRefreshValidTill(now.plusSeconds(refreshTokenExpiry));
        String jwtToken = JwtUtils.generateToken(user, now.toInstant(), secretString, issuer);
        String refreshToken = JwtUtils.generateRefreshToken(user, now.toInstant(), secretString, issuer);
        userRepo.saveAndFlush(user);
        return TokenInfo.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
    }

    @Override
    public User updateUser(User user) {
        return userRepo.saveAndFlush(user);
    }

    @Override
    public User getUser(String bearerToken) {
        String emailId = JwtUtils.validateAndGetSubject(bearerToken, TokenType.ACCESS_TOKEN,
                secretString, issuer);
        User user = getUserByEmail(emailId);
        Date date = JwtUtils.extractExpiration(bearerToken);
        CommonConstants.validateSessionExpiry.apply(user, date);
        return user;
    }

    @Override
    public User invalidateUserSession(User user) {
        OffsetDateTime now = OffsetDateTime.now();
        user.setSessionInvalidatesAt(now);
        user.setRefreshValidTill(now);
        return userRepo.saveAndFlush(user);
    }

    @Override
    public TokenInfo refreshToken(String emailId) {
        User user = getUserByEmail(emailId);
        CommonConstants.validateRefreshTokenExpiry.apply(user);
        OffsetDateTime now = OffsetDateTime.now();
        user.setSessionInvalidatesAt(now.plusSeconds(accessTokenExpiry));
        String jwtToken = JwtUtils.generateToken(user, now.toInstant(), secretString, issuer);
        String refreshToken = JwtUtils.generateRefreshToken(user, now.toInstant(), secretString, issuer);
        userRepo.saveAndFlush(user);
        return TokenInfo.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
    }
}
