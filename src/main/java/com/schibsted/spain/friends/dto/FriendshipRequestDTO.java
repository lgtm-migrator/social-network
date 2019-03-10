package com.schibsted.spain.friends.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FriendshipRequestDTO {

    private UserDTO requesterUser;
    private UserDTO requestedUser;
    private String status;


}
