package com.schibsted.spain.friends.repository;

import com.schibsted.spain.friends.entity.User;

public interface FriendshipRepository {

    Boolean requestFriendship(User requesterUser, User requestedUser);

    Boolean acceptFriendship(User requester, User requested);

    Boolean declineFriendship(String requester, String requested);
}
