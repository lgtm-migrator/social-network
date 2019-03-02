package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;

@RestController
@RequestMapping("/signup")
public class SignupLegacyController {

    private final SignupService signupService;

    public SignupLegacyController(@Autowired SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    Boolean signUp(@RequestParam("username") String username, @RequestHeader("X-Password") String password) throws ValidationException {
        return signupService.signup(username, password);
    }
}