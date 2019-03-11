package com.schibsted.spain.friends.repository;

import com.schibsted.spain.friends.entity.FriendshipRequest;
import com.schibsted.spain.friends.entity.User;
import com.schibsted.spain.friends.utils.exceptions.AlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.schibsted.spain.friends.entity.FriendRequestStatus.ACCEPTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class FriendshipRepositoryImplTest {

    private FriendshipRepository friendshipRepository = new FriendshipRepositoryImpl();
    private User user1 = User.builder().username("user1").build();
    private User user2 = User.builder().username("user2").build();
    private User user3 = User.builder().username("user3").build();

    @Test
    @DisplayName("should not allow two pending requests for the same user")
    void requestFriendshipTwice() {

        assertThat(friendshipRepository.requestFriendship(this.user1, user2)).isNotNull();
        assertThatExceptionOfType(AlreadyExistsException.class)
                .isThrownBy(() -> friendshipRepository.requestFriendship(user1, user2));
        assertThatExceptionOfType(AlreadyExistsException.class)
                .isThrownBy(() -> friendshipRepository.requestFriendship(user2, user1));
    }

    @Test
    @DisplayName("Accept friendship only once")
    void acceptFriendshipTwice() {
        friendshipRepository.requestFriendship(user1, user2);
        friendshipRepository.requestFriendship(user1, user3);
        assertThat(friendshipRepository.acceptFriendship(user1, user2))
                .isEqualTo(FriendshipRequest.builder()
                        .userTo(user2)
                        .userFrom(user1)
                        .status(ACCEPTED)
                        .build());
        assertThatExceptionOfType(AlreadyExistsException.class)
                .isThrownBy(() -> friendshipRepository.acceptFriendship(user1, user2));
    }

    @Test
    void requestFriendship() {
    }

    @Test
    void acceptFriendship() {
    }

    @Test
    void declineFriendship() {
        User userOne = User.builder().username("userOne").build();
        User userTwo = User.builder().username("userTwo").build();
        assertThat(friendshipRepository.declineFriendship(userOne, userTwo)).isNotNull();
    }
}