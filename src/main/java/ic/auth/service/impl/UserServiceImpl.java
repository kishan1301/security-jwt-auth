package ic.auth.service.impl;

import ic.auth.dto.TokenInfo;
import ic.auth.dto.UserInfo;
import ic.auth.entity.User;
import ic.auth.repo.UserRepo;
import ic.auth.service.UserService;
import ic.auth.utils.JwtUtils;
import ic.auth.utils.Utils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User createUser(String authHeader, UserInfo userInfo) {
        UserInfo parsedInfo = Utils.parseHeader(authHeader);
        userInfo.setEmail(parsedInfo.getEmail());
        userInfo.setPassword(parsedInfo.getPassword());
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
        user.setSessionInvalidatesAt(OffsetDateTime.now().plusMinutes(1));
        String jwtToken = JwtUtils.generateToken(user);
        userRepo.saveAndFlush(user);
        return TokenInfo.builder().accessToken(jwtToken).build();
    }

    @Override
    public User updateUser(User user) {
        return userRepo.saveAndFlush(user);
    }

    @Override
    public User getUser(String bearerToken) {
        String emailId = JwtUtils.validateAndGetSubject(bearerToken);
        return getUserByEmail(emailId);
    }
}
