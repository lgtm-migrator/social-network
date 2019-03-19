package com.schibsted.spain.friends.service;

import com.schibsted.spain.friends.dto.FriendshipRequestDTO;
import com.schibsted.spain.friends.dto.UserDTO;
import com.schibsted.spain.friends.entity.FriendRequestStatus;
import com.schibsted.spain.friends.entity.FriendshipRequest;
import com.schibsted.spain.friends.entity.User;
import com.schibsted.spain.friends.repository.FriendshipRepositoryImpl;
import com.schibsted.spain.friends.repository.UserRepositoryImpl;
import com.schibsted.spain.friends.utils.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class FriendshipServiceImplTest {

    private static final String JOHN_DOE_STR = "johnDoe";
    private static final String SAMANTHA_STR = "samantha";
    @Mock
    private UserRepositoryImpl userRepository;

    @Mock
    private FriendshipRepositoryImpl friendshipRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private FriendshipServiceImpl friendshipService;

    private User johnDoe = User.builder().username(JOHN_DOE_STR).build();
    private User samantha = User.builder().username(SAMANTHA_STR).build();

    @BeforeEach
    void setup() {
        friendshipService = new FriendshipServiceImpl(userRepository, friendshipRepository, userService);
        when(userRepository.getUser(anyString())).thenAnswer(invocation -> {
            if (JOHN_DOE_STR.equals(invocation.getArgument(0))) {
                return johnDoe;
            } else if (SAMANTHA_STR.equals(invocation.getArgument(0))) {
                return samantha;
            } else return null;
        });
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void selfRequest() {
        assertThatExceptionOfType(BusinessException.class).isThrownBy(() -> friendshipService.requestFriendShip("selfUser", "selfUser"));
    }

    @Test
    public void listFriends() {

        when(userRepository.getUser(SAMANTHA_STR))
                .thenReturn(User.builder().username(SAMANTHA_STR)
                        .friend(User.builder().username("peter").build())
                        .friend(User.builder().username("james").build())
                        .build());

        assertThat(friendshipService.listFriends(SAMANTHA_STR)).contains("peter", "james");
    }

    @Test
    public void requestFriendShip() {


        when(friendshipRepository.requestFriendship(johnDoe, samantha))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johnDoe)
                        .userTo(samantha)
                        .status(FriendRequestStatus.PENDING)
                        .build());

        assertThat(friendshipService.requestFriendShip(JOHN_DOE_STR, SAMANTHA_STR))
                .isEqualTo(FriendshipRequestDTO.builder()
                        .userFrom(UserDTO.builder().username(JOHN_DOE_STR).build())
                        .userTo(UserDTO.builder().username(SAMANTHA_STR).build())
                        .status(FriendRequestStatus.PENDING.name())
                        .build());
    }

    @Test
    public void acceptFriendShip() {
        when(friendshipRepository.acceptFriendship(johnDoe, samantha))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johnDoe)
                        .userTo(samantha)
                        .status(FriendRequestStatus.ACCEPTED)
                        .build());

        assertThat(friendshipService.acceptFriendShip(JOHN_DOE_STR, SAMANTHA_STR))
                .isEqualTo(FriendshipRequestDTO.builder()
                        .userFrom(UserDTO.builder().username(JOHN_DOE_STR).build())
                        .userTo(UserDTO.builder().username(SAMANTHA_STR).build())
                        .status(FriendRequestStatus.ACCEPTED.name())
                        .build());
    }

    @Test
    public void declineFriendShip() {

        when(friendshipRepository.declineFriendship(johnDoe, samantha))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johnDoe)
                        .userTo(samantha)
                        .status(FriendRequestStatus.DECLINED)
                        .build());

        assertThat(friendshipService.declineFriendShip(JOHN_DOE_STR, SAMANTHA_STR))
                .isEqualTo(FriendshipRequestDTO.builder()
                        .userFrom(UserDTO.builder().username(JOHN_DOE_STR).build())
                        .userTo(UserDTO.builder().username(SAMANTHA_STR).build())
                        .status(FriendRequestStatus.DECLINED.name())
                        .build());
    }
}