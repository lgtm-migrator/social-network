package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.service.SignupService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signup")
public class SignupLegacyController {

    private final SignupService signupService;

    public SignupLegacyController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    void signUp(@RequestParam("username") String username, @RequestHeader("X-Password") String password) {
        signupService.signup(username, password);
    }
}
