package com.schibsted.spain.friends.service;

import com.google.common.base.Preconditions;
import com.schibsted.spain.friends.repository.UserRepository;
import com.schibsted.spain.friends.utils.exceptions.AlreadyExistsException;
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
    public Boolean signup(String username, String password) throws ValidationException {
        try {
            Preconditions.checkNotNull(username, "Username cannot be null");
            Preconditions.checkNotNull(password, "Password cannot be null");
            Preconditions.checkArgument(username.matches("[a-zA-Z0-9]{5,10}+"), "Invalid format: username");
            Preconditions.checkArgument(password.matches("[a-zA-Z0-9]{8,12}+"), "Invalid format: password");
            if (log.isDebugEnabled()) {
                log.debug("Signup attempt: user {}", username);
            }
            Boolean result = userRepository.addUser(username, password);
            if (!result) {
                throw new AlreadyExistsException(String.format("User %s already exists", username));
            }
            return result;
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error("Signup error: {}", e.getMessage());
            throw new ValidationException(e.getMessage(), e);
        }
    }
}
