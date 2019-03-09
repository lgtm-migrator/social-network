package com.schibsted.spain.friends.service;

import com.schibsted.spain.friends.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FriendshipService {

    Boolean requestFriendShip(String requester, String requested);

    Boolean acceptFriendShip(String requester, String requested);

    Boolean declineFriendShip(String requester, String requested);

    List<UserDTO> listFriends(String user);
}
