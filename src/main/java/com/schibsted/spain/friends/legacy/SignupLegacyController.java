package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signup")
@Api(value = "Signup", tags = {"Signup controller"})
public class SignupLegacyController {

    private final UserService userService;

    public SignupLegacyController(@Autowired UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(nickname = "user signup", value = "user signup")
    Boolean signUp(@RequestParam("username") String username, @RequestHeader("X-Password") String password) {
        return userService.signup(username, password);
    }
}