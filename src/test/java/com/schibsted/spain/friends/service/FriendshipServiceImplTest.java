package com.schibsted.spain.friends.service;

import com.schibsted.spain.friends.dto.FriendshipRequestDTO;
import com.schibsted.spain.friends.dto.UserDTO;
import com.schibsted.spain.friends.entity.FriendRequestStatus;
import com.schibsted.spain.friends.entity.FriendshipRequest;
import com.schibsted.spain.friends.entity.User;
import com.schibsted.spain.friends.repository.FriendshipRepositoryImpl;
import com.schibsted.spain.friends.repository.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class FriendshipServiceImplTest {

    @Mock
    private UserRepositoryImpl userRepository;

    @Mock
    private FriendshipRepositoryImpl friendshipRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private FriendshipServiceImpl friendshipService;

    private User johndoe = User.builder().username("johndoe").build();
    private User samantha = User.builder().username("samantha").build();

    @BeforeEach
    void setup() {
        friendshipService = new FriendshipServiceImpl(userRepository, friendshipRepository, userService);
        when(userRepository.getUser(anyString())).thenAnswer(invocation -> {
            if ("johndoe".equals(invocation.getArgument(0))) {
                return johndoe;
            } else if ("samantha".equals(invocation.getArgument(0))) {
                return samantha;
            } else return null;
        });
    }

    @Test
    public void listFriends() {

        when(userRepository.getUser("samantha"))
                .thenReturn(User.builder().username("samantha")
                        .friend(User.builder().username("peter").build())
                        .friend(User.builder().username("james").build())
                        .build());

        assertThat(friendshipService.listFriends("samantha")).contains("peter", "james");
    }

    @Test
    public void requestFriendShip() {


        when(friendshipRepository.requestFriendship(johndoe, samantha))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johndoe)
                        .userTo(samantha)
                        .status(FriendRequestStatus.PENDING)
                        .build());

        assertThat(friendshipService.requestFriendShip("johndoe", "samantha"))
                .isEqualTo(FriendshipRequestDTO.builder()
                        .userFrom(UserDTO.builder().username("johndoe").build())
                        .userTo(UserDTO.builder().username("samantha").build())
                        .status(FriendRequestStatus.PENDING.name())
                        .build());
    }

    @Test
    public void acceptFriendShip() {
        when(friendshipRepository.acceptFriendship(johndoe, samantha))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johndoe)
                        .userTo(samantha)
                        .status(FriendRequestStatus.ACCEPTED)
                        .build());

        assertThat(friendshipService.acceptFriendShip("johndoe", "samantha"))
                .isEqualTo(FriendshipRequestDTO.builder()
                        .userFrom(UserDTO.builder().username("johndoe").build())
                        .userTo(UserDTO.builder().username("samantha").build())
                        .status(FriendRequestStatus.ACCEPTED.name())
                        .build());
    }

    @Test
    public void declineFriendShip() {

        when(friendshipRepository.declineFriendship(johndoe, samantha))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johndoe)
                        .userTo(samantha)
                        .status(FriendRequestStatus.DECLINED)
                        .build());

        assertThat(friendshipService.declineFriendShip("johndoe", "samantha"))
                .isEqualTo(FriendshipRequestDTO.builder()
                        .userFrom(UserDTO.builder().username("johndoe").build())
                        .userTo(UserDTO.builder().username("samantha").build())
                        .status(FriendRequestStatus.DECLINED.name())
                        .build());
    }
}