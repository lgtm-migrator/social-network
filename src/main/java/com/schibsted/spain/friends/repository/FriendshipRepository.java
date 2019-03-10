package com.schibsted.spain.friends.repository;

import com.schibsted.spain.friends.entity.FriendshipRequest;
import com.schibsted.spain.friends.entity.User;

public interface FriendshipRepository {

    FriendshipRequest requestFriendship(User requesterUser, User requestedUser);

    FriendshipRequest acceptFriendship(User requester, User requested);

    FriendshipRequest declineFriendship(User requester, User requested);
}
