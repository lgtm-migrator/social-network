package com.schibsted.spain.friends.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendshipRequestDTO {

    private UserDTO requesterUser;
    private UserDTO requestedUser;
    private String status;


}
