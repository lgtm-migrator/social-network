package com.schibsted.spain.friends.service;

import com.schibsted.spain.friends.dto.FriendshipRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FriendshipService {

    FriendshipRequestDTO requestFriendShip(String requester, String requested);

    FriendshipRequestDTO acceptFriendShip(String requester, String requested);

    FriendshipRequestDTO declineFriendShip(String requester, String requested);

    List<String> listFriends(String user);
}
