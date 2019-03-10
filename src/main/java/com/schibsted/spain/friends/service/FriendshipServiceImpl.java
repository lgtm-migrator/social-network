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
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    private final UserRepository userRepository;

    private final FriendshipRepository friendshipRepository;

    private final UserService userService;

    public FriendshipServiceImpl(@Autowired UserRepository userRepository, @Autowired FriendshipRepository friendshipRepository, @Autowired UserService userService) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.userService = userService;
    }

    @Override
    public FriendshipRequestDTO requestFriendShip(String requester, String requested) {
        if (requester.equals(requested)) {
            throw new BusinessException(
                    String.format("The user %s cannot make a friend request to himself", requester));
        } else {
            try {
                Tuple2<User, User> users = getUsers(requester, requested);
                User requesterUser = users._1;
                User requestedUser = users._2;
                return friendshipRepository.requestFriendship(requesterUser, requestedUser).toDTO();
            } catch (Exception e) {
                throw new NotFoundException(ErrorDto.builder()
                        .message("User not found")
                        .exceptionClass(this.getClass().getSimpleName())
                        .build());
            }
        }
    }

    @Override
    public FriendshipRequestDTO acceptFriendShip(String requester, String requested) {
        Tuple2<User, User> users = getUsers(requester, requested);
        User requesterUser = users._1;
        User requestedUser = users._2;
        final FriendshipRequest acceptFriendship = friendshipRepository.acceptFriendship(requesterUser, requestedUser);
        userService.addFriend(requester, requested);
        return acceptFriendship.toDTO();
    }

    @Override
    public FriendshipRequestDTO declineFriendShip(String requester, String requested) {
        Tuple2<User, User> users = getUsers(requester, requested);
        User requesterUser = users._1;
        User requestedUser = users._2;
        return friendshipRepository.declineFriendship(requesterUser, requestedUser).toDTO();
    }

    @Override
    public List<String> listFriends(String user) {
        try {
            UserDTO result = userRepository.getUser(user).toDto();
            return result.getFriends().stream()
                    .map(UserDTO::getUsername)
                    .sorted(Comparator.reverseOrder())//Ordering compliant
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new NotFoundException(ErrorDto.builder()
                    .message(String.format("user %s not found, cause: %s", user, e.getMessage()))
                    .exceptionClass(this.getClass().getSimpleName())
                    .build());
        }
    }

    private Tuple2<User, User> getUsers(String userFrom, String userTo) {
        return Tuple.of(userRepository.getUser(userFrom), userRepository.getUser(userTo));
    }
}
