package com.schibsted.spain.friends.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "Friend request model", description = "Model used for creating, accepting and declining friend requests")
public class FriendshipRequestDTO {

    private UserDTO userFrom;
    private UserDTO userTo;
    private String status;


}
