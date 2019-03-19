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

    private static final String PASS = "12345678";
    private static final String PASS1 = "j12345678";
    private static final String TEST_ONE = "testOne";
    private static final String TEST_TWO = "testTwo";
    private UserRepositoryImpl userRepository = new UserRepositoryImpl();

    private UserServiceImpl userService = new UserServiceImpl(userRepository);

    @Test
    void signup() {
        assertThatExceptionOfType(ValidationException.class).isThrownBy(() -> userService.signup(null, PASS));
        assertThatExceptionOfType(ValidationException.class).isThrownBy(() -> userService.signup(TEST_ONE, null));
        assertThatExceptionOfType(ValidationException.class).isThrownBy(() -> userService.signup("test", "12346578"));
        assertThatExceptionOfType(ValidationException.class).isThrownBy(() -> userService.signup(TEST_TWO, "1234657891234"));
        assertThat(userService.signup("username", PASS1))
                .isEqualTo(UserDTO.builder().username("username").password(md5DigestAsHex(PASS1.getBytes())).build());
    }

    @Test
    void addFriend() {
        UserDTO testOne = userService.signup(TEST_ONE, PASS);
        UserDTO testTwo = userService.signup(TEST_TWO, PASS);
        assertThat(userService.addFriend(TEST_ONE, TEST_TWO))
                .isEqualTo(UserDTO.builder().username(testOne.getUsername()).password(testOne.getPassword())
                        .friend(UserDTO.builder().username(testTwo.getUsername()).build()).build());

    }

    @Test
    void authenticate() {
        UserDTO testOne = userService.signup(TEST_ONE, PASS1);
        assertThat(userService.authenticate(TEST_ONE, PASS1)).isEqualTo(UserDTO.builder().username(testOne.getUsername()).password(testOne.getPassword()).build());
        assertThatExceptionOfType(InvalidCredentialException.class).isThrownBy(() -> userService.authenticate(TEST_TWO, PASS));
    }

    @Test
    void retrieveUser() {
        UserDTO testOne = userService.signup(TEST_ONE, PASS1);
        final UserDetails userDetails = userService.retrieveUser(TEST_ONE, new UsernamePasswordAuthenticationToken(TEST_ONE, PASS1));
        assertThat(userDetails.getUsername()).isEqualTo(testOne.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(testOne.getPassword());
    }
}