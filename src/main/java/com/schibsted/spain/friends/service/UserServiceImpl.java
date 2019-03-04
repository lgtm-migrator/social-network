package com.schibsted.spain.friends.service;

import com.google.common.base.Preconditions;
import com.schibsted.spain.friends.entity.User;
import com.schibsted.spain.friends.repository.UserRepository;
import com.schibsted.spain.friends.utils.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(@Autowired UserRepository userRepository) {
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
                        .message(String.format("User %s already exists", username))
                        .exceptionClass(this.getClass().getSimpleName())
                        .build());
            }
            return result;
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error("Signup error: {}", e.getMessage());
            throw new ValidationException(ErrorDto.builder()
                    .message(e.getMessage())
                    .exceptionClass(this.getClass().getSimpleName())
                    .build());
        }
    }

    /**
     * Add Users as friends to each other
     *
     * @param username  - user
     * @param username2 - useer
     * @return true if Successful operation
     */
    @Override
    public Boolean addFriend(String username, String username2) {
        User userFriend = userRepository.getUser(username);
        User userFriend2 = userRepository.getUser(username2);

        userFriend.getFriends().add(userFriend2);
        userFriend2.getFriends().add(userFriend);
        return userRepository.updateUser(userFriend) && userRepository.updateUser(userFriend2);
    }

    /**
     * Looks for a user by its username an password
     * @param username username
     * @param password password
     * @return true if the user is found, an {@link UnauthorizedException} otherwise
     */
    @Override
    public Boolean authenticate(String username, String password) {
        User user;
        try {
            user = userRepository.findUser(username, password);
        } catch (NotFoundException e) {
            log.error("user {} not found", username);
            throw new UnauthorizedException(ErrorDto.builder()
                    .message(e.getMessage())
                    .exceptionClass(this.getClass().getSimpleName()).build());
        }
        return user != null;
    }
}
