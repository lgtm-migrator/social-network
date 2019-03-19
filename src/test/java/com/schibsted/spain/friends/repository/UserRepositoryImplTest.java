package com.schibsted.spain.friends.repository;

import com.schibsted.spain.friends.entity.User;
import com.schibsted.spain.friends.utils.exceptions.AlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.util.DigestUtils.md5DigestAsHex;

@DisplayName("User repository tests")
class UserRepositoryImplTest {

    private static final String PASSWORD = "12345678";
    private UserRepositoryImpl userRepository = new UserRepositoryImpl();

    @Test
    @DisplayName("should insert unique users by their username")
    void addUser() {
        assertThat(userRepository.addUser("user", "1234")).isNotNull();
        assertThat(userRepository.addUser("user2", "12345")).isNotNull();
        assertThat(userRepository.getUsers().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should not allow to insert duplicated users")
    void addFriendTwice() {
        userRepository.addUser("12345", PASSWORD);
        assertThatExceptionOfType(AlreadyExistsException.class).isThrownBy(() -> userRepository.addUser("12345", PASSWORD));
        assertThat(userRepository.getUsers()).isNotNull();
        assertThat(userRepository.getUsers().size()).isEqualTo(1);
    }

    @Test
    void addFriend() {
        User friend = User.builder().username("test2").build();
        User friend2 = User.builder().username("test3").build();
        User user = User.builder().username("test").password(PASSWORD).friend(friend2).build();
        userRepository.addFriend(user, friend);
        final User testUser1 = userRepository.getUser("test");
        final User testUser2 = userRepository.getUser("test2");
        assertThat(testUser1.getFriends().size()).isEqualTo(2);
        assertThat(testUser1.getFriends()).contains(friend, friend2);
        assertThat(testUser2.getFriends().size()).isEqualTo(1);
        assertThat(testUser2.getFriends()).contains(user);
    }

    @Test
    void getUser() {
        userRepository.addUser("peter", PASSWORD);
        assertThat(userRepository.getUser("peter"))
                .isEqualTo(User.builder().username("peter").password(PASSWORD).build());
    }

    @Test
    void findUser() {
        userRepository.addUser("james", PASSWORD);

        assertThat(userRepository.findUser("james", PASSWORD))
                .isEqualTo(User.builder().username("james").password(md5DigestAsHex(PASSWORD.getBytes())).build());
    }

    @Test
    void updateUser() {
        User james = User.builder().username("james").password(PASSWORD).build();
        User john = User.builder().username("john").password(PASSWORD).build();
        userRepository.addUser("james", PASSWORD);
        userRepository.addFriend(james, john);
        assertThat(userRepository.updateUser(james)).isEqualTo(james.toBuilder().friend(john).build());
    }
}