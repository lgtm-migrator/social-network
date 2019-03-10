package com.schibsted.spain.friends.service;

import com.schibsted.spain.friends.repository.FriendshipRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class FriendshipServiceImplTest {

    @InjectMocks
    FriendshipServiceImpl friendshipService;

    @Mock
    FriendshipRepository friendshipRepository;

    @Test
    void requestFriendShip() {
        friendshipService.requestFriendShip("johndoe", "samantha");
        //Mockito.verify(friendshipRepository.requestFriendship("johndoe", "samantha"));
    }

    @Test
    void acceptFriendShip() {
    }

    @Test
    void declineFriendShip() {
    }

    @Test
    void listFriends() {
    }
}