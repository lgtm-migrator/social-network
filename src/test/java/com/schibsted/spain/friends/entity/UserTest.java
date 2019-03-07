package com.schibsted.spain.friends.entity;

import com.schibsted.spain.friends.dto.UserDTO;
import org.junit.jupiter.api.Test;

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
}