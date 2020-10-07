package com.schibsted.spain.friends.service;

import com.schibsted.spain.friends.dto.FriendshipRequestDTO;
import com.schibsted.spain.friends.dto.UserDTO;
import com.schibsted.spain.friends.entity.FriendshipRequest;
import com.schibsted.spain.friends.entity.User;
import com.schibsted.spain.friends.repository.FriendshipRepository;
import com.schibsted.spain.friends.repository.UserRepository;
import com.schibsted.spain.friends.utils.exceptions.BusinessException;
import com.schibsted.spain.friends.utils.exceptions.ErrorDto;
import com.schibsted.spain.friends.utils.exceptions.NotFoundException;
import com.schibsted.spain.friends.utils.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FriendshipServiceImpl implements FriendshipService {

    private final UserRepository userRepository;

    private final FriendshipRepository friendshipRepository;

    private final UserService userService;

    public FriendshipServiceImpl(@Autowired UserRepository userRepository, @Autowired FriendshipRepository friendshipRepository, @Autowired UserService userService) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.userService = userService;
    }

    /**
     * Creates a friendship request an returns a DTO.
     *
     * @param from requesting user
     * @param to   requested user
     * @return a Data Transfer object of the friendship request
     */
    @Override
    public FriendshipRequestDTO requestFriendShip(String from, String to) {
        if (from.equals(to)) {
            throw new BusinessException(
                    String.format("The user %s cannot make a friend request to himself", from));
        } else {
            try {
                User userFrom = userRepository.getUser(from);
                User userto = userRepository.getUser(to);
                return friendshipRepository.requestFriendship(userFrom, userto).toDTO();
            } catch (Exception e) {
                log.error("Exception found {}", e.getMessage());
                throw new ValidationException(ErrorDto.builder()
                        .message("User not found")
                        .exceptionClass(this.getClass().getSimpleName())
                        .build());
            }
        }
    }

    /**
     * Accepts a pending friend request. Calls the {@link UserService#addFriend(String, String)} to update the users
     *
     * @param from Requester user
     * @param to   requested user
     * @return a Friendship request DTO
     */
    @Override
    public FriendshipRequestDTO acceptFriendShip(String from, String to) {
        User userFrom = userRepository.getUser(from);
        User userTo = userRepository.getUser(to);
        final FriendshipRequest acceptFriendship = friendshipRepository.acceptFriendship(userFrom, userTo);
        userService.addFriend(from, to);
        return acceptFriendship.toDTO();
    }

    /**
     * Declines a pending friend request
     *
     * @param from requesting user
     * @param to   requested user
     * @return a friend request DTO
     */
    @Override
    public FriendshipRequestDTO declineFriendShip(String from, String to) {
        User userFrom = userRepository.getUser(from);
        User userTo = userRepository.getUser(to);
        return friendshipRepository.declineFriendship(userFrom, userTo).toDTO();
    }

    /**
     * Provides a list of friends of the specified user
     *
     * @param user user to get friends from
     * @return a list of friends
     */
    @Override
    public List<String> listFriends(String user) {
        try {
            UserDTO result = userRepository.getUser(user).toDto();
            return result.getFriends().stream()
                    .map(UserDTO::getUsername)
                    .sorted(Comparator.reverseOrder())//Ordering compliant
                    .collect(Collectors.toList());
        } catch (NotFoundException e) {
            throw new NotFoundException(ErrorDto.builder()
                    .message(String.format("user %s not found, cause: %s", user, e.getMessage()))
                    .exceptionClass(this.getClass().getSimpleName())
                    .build());
        }
    }
}
