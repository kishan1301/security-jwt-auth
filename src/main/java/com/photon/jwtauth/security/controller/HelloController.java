package com.photon.jwtauth.security.controller;

import com.photon.jwtauth.security.service.UserDetailsService;
import com.photon.jwtauth.security.util.JwtUtils;
import models.AuthRequest;
import models.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    @Autowired
    public HelloController(AuthenticationManager authManager, UserDetailsService userDetailsService, JwtUtils jwtUtils) {
        this.authManager = authManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("hello")
    public String greetings() {
        return "Welcome User!";
    }

    @PostMapping("authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest authRequest) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        final var userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final var jwtToken = jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }
}
