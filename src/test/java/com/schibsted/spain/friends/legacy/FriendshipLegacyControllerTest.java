package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.entity.FriendshipRequest;
import com.schibsted.spain.friends.entity.User;
import com.schibsted.spain.friends.repository.FriendshipRepository;
import com.schibsted.spain.friends.repository.UserRepository;
import com.schibsted.spain.friends.service.FriendshipService;
import com.schibsted.spain.friends.service.FriendshipServiceImpl;
import com.schibsted.spain.friends.service.UserService;
import com.schibsted.spain.friends.utils.exceptions.AlreadyExistsException;
import com.schibsted.spain.friends.utils.exceptions.InvalidCredentialException;
import com.schibsted.spain.friends.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.schibsted.spain.friends.entity.FriendRequestStatus.*;
import static com.schibsted.spain.friends.utils.Utils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class FriendshipLegacyControllerTest {

    private static final String JOHN_DOE_STR = "johnDoe";
    private static final String JOHN_PASS = "j12345678";
    private static final String ROSEANNE_STR = "roseanne";
    private static final String SAMANTHA_STR = "samantha";
    private static final String ROBERT_STR = "robert";
    private static final String PETER_STR = "peter";
    private static final String JESSICA_STR = "jessica";
    private static final String ROSS_PASS = "r3456789";
    private static final String PETER_PASS = "p4567890";
    private static final String ROB_PASS = "r0123456";
    private static final String DOESNT_EXISTS = " doesn't exists";
    private static final String INVALID_CREDENTIALS = "Invalid credentials for ";
    private static final String A_PENDING_FRIEND_REQUEST_ALREADY_EXISTS1 = "A pending friend request already exists";

    @Autowired
    UserService userService;
    @Autowired
    FriendshipService friendshipService;
    @MockBean
    UserRepository userRepository;
    @MockBean
    FriendshipRepository friendshipRepository;
    @Autowired
    private MockMvc mockMvc;

    private final User roseanne = User.builder().username(ROSEANNE_STR).password(ROSS_PASS).build();
    private final User robert = User.builder().username(ROBERT_STR).build();
    private final User johnDoe = User.builder().username(JOHN_DOE_STR).password(JOHN_PASS).build();
    private final User peter = User.builder().username(PETER_STR).password(PETER_PASS).build();
    private final User jessica = User.builder().username(JESSICA_STR).password(JOHN_PASS).build();

    @BeforeEach
    void setup() {
        friendshipService = new FriendshipServiceImpl(userRepository, friendshipRepository, userService);
        when(userRepository.findUser(JOHN_DOE_STR, JOHN_PASS)).thenReturn(User.builder().username(JOHN_DOE_STR).password(JOHN_PASS).build());
        when(userRepository.findUser(JOHN_DOE_STR, "j12345679")).thenThrow(new InvalidCredentialException(INVALID_CREDENTIALS + JOHN_DOE_STR));

        when(userRepository.findUser(SAMANTHA_STR, "x12345678")).thenThrow(new InvalidCredentialException(SAMANTHA_STR + DOESNT_EXISTS));

        when(userRepository.findUser(ROSEANNE_STR, ROSS_PASS)).thenReturn(roseanne);
        when(userRepository.findUser(ROSEANNE_STR, "r3456780")).thenThrow(new InvalidCredentialException(INVALID_CREDENTIALS + ROSEANNE_STR));

        when(userRepository.findUser(ROBERT_STR, ROB_PASS)).thenReturn(robert);

        when(userRepository.findUser(PETER_STR, PETER_PASS)).thenReturn(peter);
        when(userRepository.findUser(PETER_STR, "p4567891")).thenThrow(new InvalidCredentialException(INVALID_CREDENTIALS + PETER_STR));

        when(userRepository.getUser(JOHN_DOE_STR)).thenReturn(johnDoe);
        when(userRepository.getUser(ROSEANNE_STR)).thenReturn(roseanne);

    }

    @Test
    @DisplayName("should reject friend requests from invalid credentials or non-existing users")
    void friendshipRequestErrorTestCases() throws Exception {
        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, JOHN_DOE_STR)
                .param(USERNAME_TO, SAMANTHA_STR)
                .header(X_PASS, "j12345679"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, JOHN_DOE_STR)
                .param(USERNAME_TO, SAMANTHA_STR)
                .header(X_PASS, JOHN_PASS))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should be able to do a friend request only once")
    void friendRequestTestCase() throws Exception {
        when(userRepository.getUser(SAMANTHA_STR)).thenThrow(new NotFoundException(SAMANTHA_STR + DOESNT_EXISTS));
        when(friendshipRepository.requestFriendship(johnDoe, roseanne))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johnDoe)
                        .userTo(roseanne)
                        .status(PENDING)
                        .build())
                .thenThrow(new AlreadyExistsException("Friend request already exists"));

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, JOHN_DOE_STR)
                .param(USERNAME_TO, ROSEANNE_STR)
                .header(X_PASS, JOHN_PASS))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, JOHN_DOE_STR)
                .param(USERNAME_TO, ROSEANNE_STR)
                .header(X_PASS, JOHN_PASS))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should support for creating friend request to different users")
    void successfulFriendRequests() throws Exception {
        when(userRepository.getUser(PETER_STR)).thenReturn(peter);
        when(userRepository.getUser(JESSICA_STR)).thenReturn(jessica);

        when(friendshipRepository.requestFriendship(johnDoe, peter))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johnDoe)
                        .userTo(peter)
                        .status(PENDING)
                        .build());
        when(friendshipRepository.requestFriendship(johnDoe, jessica))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johnDoe)
                        .userTo(peter)
                        .status(PENDING)
                        .build());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, JOHN_DOE_STR)
                .param(USERNAME_TO, PETER_STR)
                .header(X_PASS, JOHN_PASS))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, JOHN_DOE_STR)
                .param(USERNAME_TO, JESSICA_STR)
                .header(X_PASS, JOHN_PASS))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("should support creating friendship requests from several different users")
    void friendshipRequestSuccessfulTestCases() throws Exception {
        when(userRepository.getUser(PETER_STR)).thenReturn(peter);
        when(userRepository.getUser(JESSICA_STR)).thenReturn(jessica);

        when(friendshipRepository.requestFriendship(johnDoe, roseanne))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johnDoe)
                        .userTo(roseanne)
                        .status(PENDING)
                        .build())
                .thenThrow(new AlreadyExistsException("Friend request already exists"));

        when(friendshipRepository.requestFriendship(johnDoe, peter))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johnDoe)
                        .userTo(peter)
                        .status(PENDING)
                        .build());

        when(friendshipRepository.requestFriendship(johnDoe, jessica))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johnDoe)
                        .userTo(jessica)
                        .status(PENDING)
                        .build());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, JOHN_DOE_STR)
                .param(USERNAME_TO, ROSEANNE_STR)
                .header(X_PASS, JOHN_PASS))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, JOHN_DOE_STR)
                .param(USERNAME_TO, ROSEANNE_STR)
                .header(X_PASS, JOHN_PASS))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, JOHN_DOE_STR)
                .param(USERNAME_TO, PETER_STR)
                .header(X_PASS, JOHN_PASS))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, JOHN_DOE_STR)
                .param(USERNAME_TO, JESSICA_STR)
                .header(X_PASS, JOHN_PASS))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    @DisplayName("should be able to Accept friend requests")
    void acceptFriendshipRequest() throws Exception {
        when(userRepository.getUser(PETER_STR)).thenReturn(peter);
        when(friendshipRepository.acceptFriendship(roseanne, johnDoe))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(roseanne)
                        .userTo(johnDoe)
                        .status(ACCEPTED)
                        .build())
                .thenThrow(new AlreadyExistsException(A_PENDING_FRIEND_REQUEST_ALREADY_EXISTS1));

        when(userRepository.addFriend(roseanne, johnDoe)).thenReturn(roseanne);

        mockMvc.perform(post(FRIENDSHIP_MAPPING + ACCEPT)
                .param(USERNAME_FROM, ROSEANNE_STR)
                .param(USERNAME_TO, JOHN_DOE_STR)
                .header(X_PASS, ROSS_PASS))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + ACCEPT)
                .param(USERNAME_FROM, ROSEANNE_STR)
                .param(USERNAME_TO, JOHN_DOE_STR)
                .header(X_PASS, ROSS_PASS))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + ACCEPT)
                .param(USERNAME_FROM, ROSEANNE_STR)
                .param(USERNAME_TO, JOHN_DOE_STR)
                .header(X_PASS, "r3456780"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + ACCEPT)
                .param(USERNAME_FROM, ROSEANNE_STR)
                .param(USERNAME_TO, PETER_STR)
                .header(X_PASS, ROSS_PASS))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should be able to Decline friendship request")
    void declineFriendshipRequest() throws Exception {
        when(userRepository.getUser(PETER_STR)).thenReturn(peter);
        when(friendshipRepository.declineFriendship(peter, johnDoe))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(peter)
                        .userTo(johnDoe)
                        .status(DECLINED)
                        .build())
                .thenThrow(new AlreadyExistsException(A_PENDING_FRIEND_REQUEST_ALREADY_EXISTS1));

        when(friendshipRepository.declineFriendship(peter, jessica))
                .thenThrow(new NotFoundException(A_PENDING_FRIEND_REQUEST_ALREADY_EXISTS1));

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param(USERNAME_FROM, PETER_STR)
                .param(USERNAME_TO, JOHN_DOE_STR)
                .header(X_PASS, PETER_PASS))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param(USERNAME_FROM, PETER_STR)
                .param(USERNAME_TO, JOHN_DOE_STR)
                .header(X_PASS, PETER_PASS))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param(USERNAME_FROM, PETER_STR)
                .param(USERNAME_TO, JESSICA_STR)
                .header(X_PASS, "p4567891"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param(USERNAME_FROM, PETER_STR)
                .param(USERNAME_TO, JESSICA_STR)
                .header(X_PASS, PETER_PASS))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should  support request/decline/request/accept friend request lifecycle")
    void friendshipRequestLifecycle() throws Exception {
        when(userRepository.getUser(ROBERT_STR)).thenReturn(robert);

        final FriendshipRequest pendingFriendRequest = FriendshipRequest.builder()
                .userFrom(johnDoe)
                .userTo(robert)
                .status(PENDING)
                .build();
        when(friendshipRepository.requestFriendship(johnDoe, robert))
                .thenReturn(pendingFriendRequest)
                .thenReturn(pendingFriendRequest)
                .thenThrow(new AlreadyExistsException("An accepted friend request already exists"));

        when(userRepository.addFriend(robert, johnDoe))
                .thenReturn(User.builder()
                        .username(ROBERT_STR)
                        .friend(johnDoe)
                        .build());

        when(friendshipRepository.declineFriendship(robert, johnDoe))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(robert)
                        .userTo(johnDoe)
                        .status(DECLINED)
                        .build());

        when(friendshipRepository.acceptFriendship(robert, johnDoe))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(robert)
                        .userTo(johnDoe)
                        .status(ACCEPTED)
                        .build());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, JOHN_DOE_STR)
                .param(USERNAME_TO, ROBERT_STR)
                .header(X_PASS, JOHN_PASS))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param(USERNAME_FROM, ROBERT_STR)
                .param(USERNAME_TO, JOHN_DOE_STR)
                .header(X_PASS, ROB_PASS))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, JOHN_DOE_STR)
                .param(USERNAME_TO, ROBERT_STR)
                .header(X_PASS, JOHN_PASS))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + ACCEPT)
                .param(USERNAME_FROM, ROBERT_STR)
                .param(USERNAME_TO, JOHN_DOE_STR)
                .header(X_PASS, ROB_PASS))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, JOHN_DOE_STR)
                .param(USERNAME_TO, ROBERT_STR)
                .header(X_PASS, JOHN_PASS))
                .andExpect(status().isBadRequest());

        verify(friendshipRepository, times(3)).requestFriendship(any(User.class), any(User.class));
    }

    @Test
    @DisplayName("should be able to list friends of signed in users")
    void listFriends() throws Exception {
        when(userRepository.getUser(SAMANTHA_STR)).thenThrow(new NotFoundException(SAMANTHA_STR + DOESNT_EXISTS));
        when(userRepository.getUser(JOHN_DOE_STR))
                .thenReturn(User.builder().username(JOHN_DOE_STR)
                        .friend(roseanne)
                        .friend(robert)
                        .build());

        when(userRepository.getUser(ROSEANNE_STR))
                .thenReturn(User.builder().username(ROSEANNE_STR).friend(johnDoe).build());

        when(userRepository.getUser(ROBERT_STR))
                .thenReturn(User.builder().username(ROBERT_STR).friend(johnDoe).build());

        when(userRepository.getUser(PETER_STR)).thenReturn(peter);

        mockMvc.perform(get(FRIENDSHIP_MAPPING + LIST)
                .param(USERNAME, JOHN_DOE_STR)
                .header(X_PASS, "j12345679"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get(FRIENDSHIP_MAPPING + LIST)
                .param(USERNAME, SAMANTHA_STR)
                .header(X_PASS, "x12345678"))
                .andExpect(status().isUnauthorized());

        testFindFriends(JOHN_DOE_STR, JOHN_PASS, "[\"roseanne\",\"robert\"]");

        testFindFriends(ROSEANNE_STR, ROSS_PASS, "[\"johnDoe\"]");

        testFindFriends(ROBERT_STR, ROB_PASS, "[\"johnDoe\"]");

        testFindFriends(PETER_STR, PETER_PASS, "[]");
    }

    private void testFindFriends(String user, String password, String result) throws Exception {
        final String response = mockMvc.perform(get(FRIENDSHIP_MAPPING + LIST)
                .param(USERNAME, user)
                .header(X_PASS, password))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo(result);
    }
}