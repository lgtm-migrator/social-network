package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.entity.FriendshipRequest;
import com.schibsted.spain.friends.entity.User;
import com.schibsted.spain.friends.repository.FriendshipRepository;
import com.schibsted.spain.friends.repository.UserRepository;
import com.schibsted.spain.friends.service.FriendshipService;
import com.schibsted.spain.friends.service.FriendshipServiceImpl;
import com.schibsted.spain.friends.service.UserService;
import com.schibsted.spain.friends.utils.Utils;
import com.schibsted.spain.friends.utils.exceptions.AlreadyExistsException;
import com.schibsted.spain.friends.utils.exceptions.InvalidCredentialException;
import com.schibsted.spain.friends.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger log = LoggerFactory.getLogger(FriendshipLegacyControllerTest.class);

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

    private final User roseanne = User.builder().username("roseanne").password("r3456789").build();
    private final User robert = User.builder().username("robert").build();
    private final User johndoe = User.builder().username("johndoe").password("j12345678").build();
    private final User peter = User.builder().username("peter").password("p4567890").build();
    private final User jessica = User.builder().username("jessica").password("j12345678").build();

    @BeforeEach
    void setup() {
        friendshipService = new FriendshipServiceImpl(userRepository, friendshipRepository, userService);
        when(userRepository.findUser("johndoe", "j12345678")).thenReturn(User.builder().username("johndoe").password("j12345678").build());
        when(userRepository.findUser("johndoe", "j12345679")).thenThrow(new InvalidCredentialException("Invalid credentials for John Doe"));

        when(userRepository.findUser("samantha", "x12345678")).thenThrow(new InvalidCredentialException("Samantha doesn't exists"));

        when(userRepository.findUser("roseanne", "r3456789")).thenReturn(roseanne);
        when(userRepository.findUser("roseanne", "r3456780")).thenThrow(new InvalidCredentialException("Invalid credentials for Roseanne"));

        when(userRepository.findUser("robert", "r0123456")).thenReturn(robert);

        when(userRepository.findUser("peter", "p4567890")).thenReturn(peter);
        when(userRepository.findUser("peter", "p4567891")).thenThrow(new InvalidCredentialException("Invalid credentials for Peter"));

        when(userRepository.getUser("johndoe")).thenReturn(johndoe);
        when(userRepository.getUser("roseanne")).thenReturn(roseanne);

    }

    @Test
    @DisplayName("should reject friend requests from invalid credentials or non-existing users")
    void friendshipRequestErrorTestCases() throws Exception {
        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, "johndoe")
                .param(USERNAME_TO, "samantha")
                .header(X_PASS, "j12345679"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, "johndoe")
                .param(USERNAME_TO, "samantha")
                .header(X_PASS, "j12345678"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should be able to do a friend request only once")
    void friendRequestTestCase() throws Exception {
        when(userRepository.getUser("samantha")).thenThrow(new NotFoundException("Samantha doesn't exists"));
        when(friendshipRepository.requestFriendship(johndoe, roseanne))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johndoe)
                        .userTo(roseanne)
                        .status(PENDING)
                        .build())
                .thenThrow(new AlreadyExistsException("Friend request already exists"));

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, "johndoe")
                .param(USERNAME_TO, "roseanne")
                .header(X_PASS, "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, "johndoe")
                .param(USERNAME_TO, "roseanne")
                .header(X_PASS, "j12345678"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should support for creating friend request to different users")
    void successfulFriendRequests() throws Exception {
        when(userRepository.getUser("peter")).thenReturn(peter);
        when(userRepository.getUser("jessica")).thenReturn(jessica);

        when(friendshipRepository.requestFriendship(johndoe, peter))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johndoe)
                        .userTo(peter)
                        .status(PENDING)
                        .build());
        when(friendshipRepository.requestFriendship(johndoe, jessica))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johndoe)
                        .userTo(peter)
                        .status(PENDING)
                        .build());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, "johndoe")
                .param(USERNAME_TO, "peter")
                .header(X_PASS, "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, "johndoe")
                .param(USERNAME_TO, "jessica")
                .header(X_PASS, "j12345678"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("should support creating friendship requests from several different users")
    void friendshipRequestSuccessfulTestCases() throws Exception {
        when(userRepository.getUser("peter")).thenReturn(peter);
        when(userRepository.getUser("jessica")).thenReturn(jessica);

        when(friendshipRepository.requestFriendship(johndoe, roseanne))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johndoe)
                        .userTo(roseanne)
                        .status(PENDING)
                        .build())
                .thenThrow(new AlreadyExistsException("Friend request already exists"));

        when(friendshipRepository.requestFriendship(johndoe, peter))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johndoe)
                        .userTo(peter)
                        .status(PENDING)
                        .build());

        when(friendshipRepository.requestFriendship(johndoe, jessica))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(johndoe)
                        .userTo(jessica)
                        .status(PENDING)
                        .build());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, "johndoe")
                .param(USERNAME_TO, "roseanne")
                .header(X_PASS, "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, "johndoe")
                .param(USERNAME_TO, "roseanne")
                .header(X_PASS, "j12345678"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, "johndoe")
                .param(USERNAME_TO, "peter")
                .header(X_PASS, "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, "johndoe")
                .param(USERNAME_TO, "jessica")
                .header(X_PASS, "j12345678"))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    @DisplayName("should be able to Accept friend requests")
    void acceptFriendshipRequest() throws Exception {
        when(userRepository.getUser("peter")).thenReturn(peter);
        when(friendshipRepository.acceptFriendship(roseanne, johndoe))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(roseanne)
                        .userTo(johndoe)
                        .status(ACCEPTED)
                        .build())
                .thenThrow(new AlreadyExistsException("A pending friend request already exists"));

        when(userRepository.addFriend(roseanne, johndoe)).thenReturn(roseanne);

        mockMvc.perform(post(FRIENDSHIP_MAPPING + ACCEPT)
                .param(USERNAME_FROM, "roseanne")
                .param(USERNAME_TO, "johndoe")
                .header(X_PASS, "r3456789"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + ACCEPT)
                .param(USERNAME_FROM, "roseanne")
                .param(USERNAME_TO, "johndoe")
                .header(X_PASS, "r3456789"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + ACCEPT)
                .param(USERNAME_FROM, "roseanne")
                .param(USERNAME_TO, "johndoe")
                .header(X_PASS, "r3456780"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + ACCEPT)
                .param(USERNAME_FROM, "roseanne")
                .param(USERNAME_TO, "peter")
                .header(X_PASS, "r3456789"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should be able to Decline friendship request")
    void declineFriendshipRequest() throws Exception {
        when(userRepository.getUser("peter")).thenReturn(peter);
        when(friendshipRepository.declineFriendship(peter, johndoe))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(peter)
                        .userTo(johndoe)
                        .status(DECLINED)
                        .build())
                .thenThrow(new AlreadyExistsException("A pending friend request already exists"));

        when(friendshipRepository.declineFriendship(peter, jessica))
                .thenThrow(new NotFoundException("A pending friend request already exists"));

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param(USERNAME_FROM, "peter")
                .param(USERNAME_TO, "johndoe")
                .header(X_PASS, "p4567890"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param(USERNAME_FROM, "peter")
                .param(USERNAME_TO, "johndoe")
                .header(X_PASS, "p4567890"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param(USERNAME_FROM, "peter")
                .param(USERNAME_TO, "jessica")
                .header(X_PASS, "p4567891"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param(USERNAME_FROM, "peter")
                .param(USERNAME_TO, "jessica")
                .header(X_PASS, "p4567890"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should  support request/decline/request/accept friend request lifecycle")
    void friendshipRequestLifecycle() throws Exception {
        when(userRepository.getUser("robert")).thenReturn(robert);

        final FriendshipRequest pendingFriendRequest = FriendshipRequest.builder()
                .userFrom(johndoe)
                .userTo(robert)
                .status(PENDING)
                .build();
        when(friendshipRepository.requestFriendship(johndoe, robert))
                .thenReturn(pendingFriendRequest)
                .thenReturn(pendingFriendRequest)
                .thenThrow(new AlreadyExistsException("An accepted friend request already exists"));

        when(userRepository.addFriend(robert, johndoe))
                .thenReturn(User.builder()
                        .username("robert")
                        .friend(johndoe)
                        .build());

        when(friendshipRepository.declineFriendship(robert, johndoe))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(robert)
                        .userTo(johndoe)
                        .status(DECLINED)
                        .build());

        when(friendshipRepository.acceptFriendship(robert, johndoe))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(robert)
                        .userTo(johndoe)
                        .status(ACCEPTED)
                        .build());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, "johndoe")
                .param(USERNAME_TO, "robert")
                .header(X_PASS, "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param(USERNAME_FROM, "robert")
                .param(USERNAME_TO, "johndoe")
                .header(X_PASS, "r0123456"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, "johndoe")
                .param(USERNAME_TO, "robert")
                .header(X_PASS, "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + ACCEPT)
                .param(USERNAME_FROM, "robert")
                .param(USERNAME_TO, "johndoe")
                .header(X_PASS, "r0123456"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param(USERNAME_FROM, "johndoe")
                .param(USERNAME_TO, "robert")
                .header(X_PASS, "j12345678"))
                .andExpect(status().isBadRequest());

        verify(friendshipRepository, times(3)).requestFriendship(any(User.class), any(User.class));
    }

    @Test
    @DisplayName("should be able to list friends of signed in users")
    void listFriends() throws Exception {
        when(userRepository.getUser("samantha")).thenThrow(new NotFoundException("Samantha doesn't exists"));
        when(userRepository.getUser("johndoe"))
                .thenReturn(User.builder().username("johndoe")
                        .friend(roseanne)
                        .friend(robert)
                        .build());

        when(userRepository.getUser("roseanne"))
                .thenReturn(User.builder().username("roseanne").friend(johndoe).build());

        when(userRepository.getUser("robert"))
                .thenReturn(User.builder().username("robert").friend(johndoe).build());

        when(userRepository.getUser("peter")).thenReturn(peter);

        mockMvc.perform(get(FRIENDSHIP_MAPPING + LIST)
                .param(USERNAME, "johndoe")
                .header(X_PASS, "j12345679"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get(FRIENDSHIP_MAPPING + LIST)
                .param(USERNAME, "samantha")
                .header(X_PASS, "x12345678"))
                .andExpect(status().isUnauthorized());

        testFindFriends("johndoe", "j12345678", "[\"roseanne\",\"robert\"]");

        testFindFriends("roseanne", "r3456789", "[\"johndoe\"]");

        testFindFriends("robert", "r0123456", "[\"johndoe\"]");

        testFindFriends("peter", "p4567890", "[]");
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