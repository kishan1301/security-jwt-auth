package ic.auth.controller;

import ic.auth.dto.TokenInfo;
import ic.auth.dto.UserInfo;
import ic.auth.service.UserService;
import ic.auth.utils.Utils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("signup")
    public ResponseEntity<UserInfo> signup(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody UserInfo userInfo
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Utils.toExternalisedUser(userService.createUser(authHeader, userInfo)));
    }

    @PostMapping("sign-in")
    public ResponseEntity<TokenInfo> signIn(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return ResponseEntity.ok(userService.getJwtToken(authHeader));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserInfo> getUserByEmail(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        return ResponseEntity.ok(Utils.toExternalisedUser(userService.getUser(bearerToken)));
    }
}
