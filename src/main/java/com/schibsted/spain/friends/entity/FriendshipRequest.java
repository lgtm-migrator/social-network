package com.schibsted.spain.friends.entity;

import com.schibsted.spain.friends.dto.FriendshipRequestDTO;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
public class FriendshipRequest {

    private User requesterUser;
    private User requestedUser;
    private FriendRequestStatus status;


    public void fromDTO(FriendshipRequestDTO friendshipRequestDTO) {
        this.status = Optional.ofNullable(friendshipRequestDTO.getStatus()).map(FriendRequestStatus::valueOf).orElse(null);
    }

    public FriendshipRequestDTO toDTO() {
        return FriendshipRequestDTO.builder()
                .requesterUser(this.requesterUser.toDto())
                .requestedUser(this.requesterUser.toDto())
                .status(this.status.name())
                .build();
    }
}
