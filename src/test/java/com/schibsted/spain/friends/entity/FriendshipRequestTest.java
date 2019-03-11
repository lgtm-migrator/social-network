package com.schibsted.spain.friends.entity;

import com.schibsted.spain.friends.dto.FriendshipRequestDTO;
import com.schibsted.spain.friends.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FriendshipRequestTest {

    @Test
    @DisplayName("from DTO conversion")
    void toDTO() {
        User requester = User.builder().username("requester").build();
        User requested = User.builder().username("requested").build();

        FriendshipRequest friendshipRequest = FriendshipRequest.builder()
                .userFrom(requester)
                .userTo(requested)
                .status(FriendRequestStatus.ACCEPTED)
                .build();

        FriendshipRequestDTO friendshipRequestDTO = friendshipRequest.toDTO();
        assertThat(friendshipRequestDTO.getUserTo().getUsername()).isEqualTo("requested");
        assertThat(friendshipRequestDTO.getUserFrom().getUsername()).isEqualTo("requester");
        assertThat(friendshipRequestDTO.getStatus()).isEqualTo("ACCEPTED");
    }

    @Test
    void fromDTO() {
        UserDTO requester = UserDTO.builder().username("requester").build();
        UserDTO requested = UserDTO.builder().username("requested").build();
        FriendshipRequestDTO friendshipRequestDTO = FriendshipRequestDTO.builder()
                .userTo(requested)
                .userFrom(requester)
                .status("PENDING").build();

        FriendshipRequest friendshipRequest = new FriendshipRequest();
        friendshipRequest.fromDTO(friendshipRequestDTO);

        assertThat(friendshipRequest.getUserTo().getUsername()).isEqualTo("requested");
        assertThat(friendshipRequest.getUserFrom().getUsername()).isEqualTo("requester");
        assertThat(friendshipRequest.getStatus()).isEqualByComparingTo(FriendRequestStatus.PENDING);
    }
}