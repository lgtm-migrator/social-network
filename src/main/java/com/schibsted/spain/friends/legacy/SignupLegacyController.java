package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.dto.UserDTO;
import com.schibsted.spain.friends.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.schibsted.spain.friends.utils.Utils.*;

@RestController
@RequestMapping(SIGN_UP_MAPPING)
@Api(value = "Signup", tags = {"Signup controller"})
public class SignupLegacyController {

    private final UserService userService;

    public SignupLegacyController(@Autowired UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(nickname = "user signup", value = "user signup")
    ResponseEntity<UserDTO> signUp(@RequestParam(USERNAME) String username, @RequestHeader(X_PASS) String password) {
        return ResponseEntity.ok(userService.signup(username, password));
    }
}