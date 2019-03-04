package com.schibsted.spain.friends.service;

import com.schibsted.spain.friends.entity.User;
import com.schibsted.spain.friends.repository.FriendshipRepository;
import com.schibsted.spain.friends.repository.UserRepository;
import com.schibsted.spain.friends.utils.exceptions.BusinessException;
import com.schibsted.spain.friends.utils.exceptions.NotFoundException;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    private final UserRepository userRepository;

    private final FriendshipRepository friendshipRepository;

    public FriendshipServiceImpl(@Autowired UserRepository userRepository,
                                 @Autowired FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    @Override
    public Boolean requestFriendShip(String requester, String requested) {
        if (requester.equals(requested)) {
            throw new BusinessException(
                    String.format("The user %s cannot make a friend request to himself", requester));
        } else {
            try {
                Tuple2<User, User> users = getUsers(requester, requested);
                User requesterUser = users._1;
                User requestedUser = users._2;
                return friendshipRepository.requestFriendship(requesterUser, requestedUser);
            } catch (Exception e) {
                throw new NotFoundException("user not found", e);
            }
        }
    }

    @Override
    public Boolean acceptFriendShip(String requester, String requested) {
        Tuple2<User, User> users = getUsers(requester, requested);
        User requesterUser = users._1;
        User requestedUser = users._2;
        return friendshipRepository.acceptFriendship(requesterUser, requestedUser);
    }

    @Override
    public Boolean declineFriendShip(String requester, String requested) {
        Tuple2<User, User> users = getUsers(requester, requested);
        User requesterUser = users._1;
        User requestedUser = users._2;
        return friendshipRepository.declineFriendship(requesterUser, requestedUser);
    }

    @Override
    public Set<User> listFriends(String user) {
        try {
            User result = userRepository.getUser(user);
            return result.getFriends();
        } catch (Exception e) {
            throw new NotFoundException(String.format("user %s not found", user), e);
        }
    }

    private Tuple2<User, User> getUsers(String userFrom, String userTo) {
        return Tuple.of(userRepository.getUser(userFrom), userRepository.getUser(userTo));
    }
}
