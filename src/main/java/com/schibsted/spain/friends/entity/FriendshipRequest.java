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

    private User userFrom;
    private User userTo;
    private FriendRequestStatus status;

    public void fromDTO(FriendshipRequestDTO friendshipRequestDTO) {
        this.userFrom = User.builder().username(friendshipRequestDTO.getUserFrom().getUsername()).build();
        this.userTo = User.builder().username(friendshipRequestDTO.getUserTo().getUsername()).build();
        this.status = Optional.ofNullable(friendshipRequestDTO.getStatus()).map(FriendRequestStatus::valueOf).orElse(null);
    }

    public FriendshipRequestDTO toDTO() {
        return FriendshipRequestDTO.builder()
                .userFrom(userFrom.toDto())
                .userTo(userTo.toDto())
                .status(status.name())
                .build();
    }
}
