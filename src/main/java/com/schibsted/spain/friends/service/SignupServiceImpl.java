package com.schibsted.spain.friends.service;

import com.google.common.base.Preconditions;
import com.schibsted.spain.friends.repository.UserRepository;
import com.schibsted.spain.friends.utils.exceptions.AlreadyExistsException;
import com.schibsted.spain.friends.utils.exceptions.ErrorDto;
import com.schibsted.spain.friends.utils.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class SignupServiceImpl implements SignupService {

    private final UserRepository userRepository;

    public SignupServiceImpl(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * User signup
     *
     * @param username username
     * @param password password
     * @return true if valid, false if invalid
     */
    @Override
    public Boolean signup(String username, String password) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Signup attempt: user {}", username);
            }
            Preconditions.checkNotNull(username, "Username cannot be null");
            Preconditions.checkNotNull(password, "Password cannot be null");
            Preconditions.checkArgument(username.matches("[a-zA-Z0-9]{5,10}+"), String.format("Invalid format: username  %s should be alphanumeric and between 5 and 10 characters", username));
            Preconditions.checkArgument(password.matches("[a-zA-Z0-9]{8,12}+"), String.format("Invalid format: password %s should be alphanumeric and between 8 and 12 characters", password));
            Boolean result = userRepository.addUser(username, password);
            if (!result) {
                throw new AlreadyExistsException(ErrorDto.builder()
                        .msg(String.format("User %s already exists", username))
                        .exceptionClass(this.getClass().getSimpleName())
                        .build());
            }
            return result;
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error("Signup error: {}", e.getMessage());
            throw new ValidationException(ErrorDto.builder()
                    .msg(e.getMessage())
                    .exceptionClass(this.getClass().getSimpleName())
                    .build());
        }
    }
}
