package com.schibsted.spain.friends.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendshipRequest {

    private User requesterUser;
    private User requestedUser;
    private FriendRequestStatus status;
}
