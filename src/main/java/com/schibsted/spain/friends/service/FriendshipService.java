package com.schibsted.spain.friends.service;

import com.schibsted.spain.friends.dto.FriendshipRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FriendshipService {

    FriendshipRequestDTO requestFriendShip(String from, String to);

    FriendshipRequestDTO acceptFriendShip(String from, String to);

    FriendshipRequestDTO declineFriendShip(String from, String to);

    List<String> listFriends(String user);
}
