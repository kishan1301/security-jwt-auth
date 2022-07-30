package com.photon.jwtauth.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("hello")
    String greetings() {
        return "Welcome User!";
    }
}
