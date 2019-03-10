package com.schibsted.spain.friends.entity;

import com.schibsted.spain.friends.dto.FriendshipRequestDTO;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
@ApiModel(value = "Friend request model", description = "Model used for creating, accepting and declining friend requests")
public class FriendshipRequest {

    private User requesterUser;
    private User requestedUser;
    private FriendRequestStatus status;

    public void fromDTO(FriendshipRequestDTO friendshipRequestDTO) {
        this.requestedUser.fromDTO(friendshipRequestDTO.getRequestedUser());
        this.requesterUser.fromDTO(friendshipRequestDTO.getRequesterUser());
        this.status = Optional.ofNullable(friendshipRequestDTO.getStatus()).map(FriendRequestStatus::valueOf).orElse(null);
    }

    public FriendshipRequestDTO toDTO() {
        return FriendshipRequestDTO.builder()
                .requesterUser(requesterUser.toDto())
                .requestedUser(requesterUser.toDto())
                .status(status.name())
                .build();
    }
}
