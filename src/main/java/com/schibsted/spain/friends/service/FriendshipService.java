package com.schibsted.spain.friends.service;

import com.schibsted.spain.friends.entity.User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface FriendshipService {

    Boolean requestFriendShip(String requester, String requested);

    Boolean acceptFriendShip(String requester, String requested);

    Boolean declineFriendShip(String requester, String requested);

    Set<User> listFriends(String user);
}
