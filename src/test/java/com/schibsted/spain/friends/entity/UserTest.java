package com.schibsted.spain.friends.entity;

import com.schibsted.spain.friends.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void toDto() {
        User user = User.builder()
                .username("test")
                .password("12345678")
                .friend(User.builder().username("test2").password("12348").build())
                .build();

        UserDTO userDTO = user.toDto();
        assertThat(userDTO).isEqualToIgnoringGivenFields(user, "friends");
        assertThat(userDTO.getFriends()).zipSatisfy(user.getFriends(), (userDTO1, user1) -> assertThat(userDTO1.getUsername()).isEqualTo(user1.getUsername()));
    }

    @Test
    void fromDTO() {
        UserDTO userDTO = UserDTO.builder()
                .username("test")
                .password("12345678")
                .friend(UserDTO.builder().username("test2").build())
                .build();

        User user = new User();
        user.fromDTO(userDTO);

        assertThat(user).isEqualToIgnoringGivenFields(userDTO, "friends");
        assertThat(user.getFriends()).zipSatisfy(userDTO.getFriends(), (user1, userDTO1) -> assertThat(user1.getUsername()).isEqualTo(userDTO1.getUsername()));
    }

    @Test
    @DisplayName("Self Recursion test")
    void recursion() {
        User johnDoe = User.builder().username("johnDoe").build();
        User roseanne = User.builder().username("roseanne").friend(johnDoe).build();
        johnDoe.setFriends(Collections.singleton(roseanne));

        UserDTO johnDTO = johnDoe.toDto();
        assertThat(johnDTO.getFriends().size()).isEqualTo(1);
        final UserDTO firstFriend = johnDTO.getFriends().get(0);
        assertThat(firstFriend).isNotNull();
        assertThat(firstFriend.getUsername()).isEqualTo(roseanne.getUsername());
    }

    @Test
    void equals() {
        User user = User.builder().username("usertest").password("123456").build();
        User user2 = User.builder().username("usertest").password("dasdsd").build();
        assertThat(user.equals(user2)).isTrue();
    }

    @Test
    void hashVerification() {
        User user = User.builder().username("test").build();
        User user2 = User.builder().username("test").build();
        assertThat(user.hashCode()).isNotNull();
        assertThat(user2.hashCode()).isEqualTo(user.hashCode());
    }
}