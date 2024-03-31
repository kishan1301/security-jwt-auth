package ic.auth.controller;

import ic.auth.dto.TokenInfo;
import ic.auth.dto.UserInfo;
import ic.auth.service.UserService;
import ic.auth.utils.Utils;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
@Tag(name = "user")
@SecurityScheme(name = "bearer-auth", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("signup")
    @SecurityRequirement(name = "basic-auth")
    public ResponseEntity<UserInfo> signup(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody UserInfo userInfo
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Utils.toExternalisedUser(userService.createUser(authHeader, userInfo)));
    }

    @PostMapping("sign-in")
    @SecurityRequirement(name = "basic-auth")
    public ResponseEntity<TokenInfo> signIn(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return ResponseEntity.ok(userService.getJwtToken(authHeader));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "bearer-auth")
    public ResponseEntity<UserInfo> getUserByEmail(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        return ResponseEntity.ok(Utils.toExternalisedUser(userService.getUser(bearerToken)));
    }
}
