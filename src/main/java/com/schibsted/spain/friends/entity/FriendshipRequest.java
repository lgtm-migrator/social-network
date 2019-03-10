package com.schibsted.spain.friends.entity;

import com.schibsted.spain.friends.dto.FriendshipRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipRequest {

    private User requesterUser;
    private User requestedUser;
    private FriendRequestStatus status;

    public void fromDTO(FriendshipRequestDTO friendshipRequestDTO) {
        this.requestedUser = User.builder().username(friendshipRequestDTO.getRequestedUser().getUsername()).build();
        this.requesterUser = User.builder().username(friendshipRequestDTO.getRequesterUser().getUsername()).build();
        this.status = Optional.ofNullable(friendshipRequestDTO.getStatus()).map(FriendRequestStatus::valueOf).orElse(null);
    }

    public FriendshipRequestDTO toDTO() {
        return FriendshipRequestDTO.builder()
                .requesterUser(requesterUser.toDto())
                .requestedUser(requestedUser.toDto())
                .status(status.name())
                .build();
    }
}
