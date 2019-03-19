package com.schibsted.spain.friends.entity;

import com.schibsted.spain.friends.dto.FriendshipRequestDTO;
import com.schibsted.spain.friends.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FriendshipRequestTest {

    private static final String REQUESTER = "requester";
    private static final String REQUESTED = "requested";

    @Test
    @DisplayName("from DTO conversion")
    void toDTO() {
        User requester = User.builder().username(REQUESTER).build();
        User requested = User.builder().username(REQUESTED).build();

        FriendshipRequest friendshipRequest = FriendshipRequest.builder()
                .userFrom(requester)
                .userTo(requested)
                .status(FriendRequestStatus.ACCEPTED)
                .build();

        FriendshipRequestDTO friendshipRequestDTO = friendshipRequest.toDTO();
        assertThat(friendshipRequestDTO.getUserTo().getUsername()).isEqualTo(REQUESTED);
        assertThat(friendshipRequestDTO.getUserFrom().getUsername()).isEqualTo(REQUESTER);
        assertThat(friendshipRequestDTO.getStatus()).isEqualTo(FriendRequestStatus.ACCEPTED.name());
    }

    @Test
    void fromDTO() {
        UserDTO requester = UserDTO.builder().username(REQUESTER).build();
        UserDTO requested = UserDTO.builder().username(REQUESTED).build();
        FriendshipRequestDTO friendshipRequestDTO = FriendshipRequestDTO.builder()
                .userTo(requested)
                .userFrom(requester)
                .status(FriendRequestStatus.PENDING.name()).build();

        FriendshipRequest friendshipRequest = new FriendshipRequest();
        friendshipRequest.fromDTO(friendshipRequestDTO);

        assertThat(friendshipRequest.getUserTo().getUsername()).isEqualTo(REQUESTED);
        assertThat(friendshipRequest.getUserFrom().getUsername()).isEqualTo(REQUESTER);
        assertThat(friendshipRequest.getStatus()).isEqualByComparingTo(FriendRequestStatus.PENDING);
    }
}