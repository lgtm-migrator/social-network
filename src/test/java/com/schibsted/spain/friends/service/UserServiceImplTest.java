package com.schibsted.spain.friends.service;

import com.schibsted.spain.friends.dto.UserDTO;
import com.schibsted.spain.friends.repository.UserRepositoryImpl;
import com.schibsted.spain.friends.utils.exceptions.InvalidCredentialException;
import com.schibsted.spain.friends.utils.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.util.DigestUtils.md5DigestAsHex;


class UserServiceImplTest {

    private UserRepositoryImpl userRepository = new UserRepositoryImpl();

    private UserServiceImpl userService = new UserServiceImpl(userRepository);

    @Test
    void signup() {
        assertThatExceptionOfType(ValidationException.class).isThrownBy(() -> userService.signup(null, "12345678"));
        assertThatExceptionOfType(ValidationException.class).isThrownBy(() -> userService.signup("testOne", null));
        assertThatExceptionOfType(ValidationException.class).isThrownBy(() -> userService.signup("test", "12346578"));
        assertThatExceptionOfType(ValidationException.class).isThrownBy(() -> userService.signup("testTwo", "1234657891234"));
        assertThat(userService.signup("username", "j12345678"))
                .isEqualTo(UserDTO.builder().username("username").password(md5DigestAsHex("j12345678".getBytes())).build());
    }

    @Test
    void addFriend() {
        UserDTO testOne = userService.signup("testOne", "12345678");
        UserDTO testTwo = userService.signup("testTwo", "12345678");
        assertThat(userService.addFriend("testOne", "testTwo"))
                .isEqualTo(UserDTO.builder().username(testOne.getUsername()).password(testOne.getPassword())
                        .friend(UserDTO.builder().username(testTwo.getUsername()).build()).build());

    }

    @Test
    void authenticate() {
        UserDTO testOne = userService.signup("testOne", "j12345678");
        assertThat(userService.authenticate("testOne", "j12345678")).isEqualTo(UserDTO.builder().username(testOne.getUsername()).password(testOne.getPassword()).build());
        assertThatExceptionOfType(InvalidCredentialException.class).isThrownBy(() -> userService.authenticate("testTwo", "12345678"));
    }

    @Test
    void retrieveUser() {
        UserDTO testOne = userService.signup("testOne", "j12345678");
        final UserDetails userDetails = userService.retrieveUser("testOne", new UsernamePasswordAuthenticationToken("testOne", "j12345678"));
        assertThat(userDetails.getUsername()).isEqualTo(testOne.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(testOne.getPassword());
    }
}