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

import static com.schibsted.spain.friends.entity.FriendRequestStatus.ACCEPTED;
import static com.schibsted.spain.friends.utils.Utils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class FriendshipLegacyControllerTest {

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

    @BeforeEach
    void setup() {
        friendshipService = new FriendshipServiceImpl(userRepository, friendshipRepository, userService);
    }


    @Test
    @DisplayName("Friendship request test cases")
    void friendshipRequestErrorTestCases() throws Exception {
        when(userRepository.findUser("johndoe", "j12345678")).thenReturn(User.builder().username("johndoe").password("j12345678").build());
        when(userRepository.findUser("johndoe", "j12345679")).thenThrow(InvalidCredentialException.class);
        mockMvc.perform(post(FRIENDSHIP_REQUEST_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "samantha")
                .header("X-Password", "j12345679"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post(FRIENDSHIP_REQUEST_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "samantha")
                .header("X-Password", "j12345678"))
                .andExpect(status().isNotFound());


    }

    @Test
    @DisplayName("should be able to do a friend request only once")
    void friendRequestTestCase() throws Exception {
        when(userRepository.findUser("johndoe", "j12345678")).thenReturn(User.builder().username("johndoe").password("j12345678").build());
        when(userRepository.getUser("johndoe")).thenReturn(User.builder().username("johndoe").password("j12345678").build());
        when(userRepository.getUser("roseanne")).thenReturn(User.builder().username("roseanne").build());
        when(userRepository.getUser("samantha")).thenThrow(NotFoundException.class);
        mockMvc.perform(post(FRIENDSHIP_REQUEST_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "roseanne")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_REQUEST_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "roseanne")
                .header("X-Password", "j12345678"))
                .andExpect(status().isNotFound());


    }

    @Test
    @DisplayName("Should support for creating friend request to different users")
    void successfulFriendRequests() throws Exception {
        when(userRepository.findUser("johndoe", "j12345678")).thenReturn(User.builder().username("johndoe").password("j12345678").build());
        when(userRepository.getUser("johndoe")).thenReturn(User.builder().username("johndoe").password("j12345678").build());
        when(userRepository.getUser("peter")).thenReturn(User.builder().username("peter").password("j12345678").build());
        when(userRepository.getUser("jessica")).thenReturn(User.builder().username("jessica").password("j12345678").build());

        mockMvc.perform(post(FRIENDSHIP_REQUEST_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "peter")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_REQUEST_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "jessica")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("successful test cases")
    void friendshipRequestSuccessfulTestCases() throws Exception {
        when(userRepository.findUser("johndoe", "j12345678")).thenReturn(User.builder().username("johndoe").password("j12345678").build());
        when(userRepository.getUser("johndoe")).thenReturn(User.builder().username("johndoe").password("j12345678").build());
        when(userRepository.getUser("roseanne")).thenReturn(User.builder().username("roseanne").password("j12345678").build());
        when(userRepository.getUser("peter")).thenReturn(User.builder().username("peter").password("j12345678").build());
        when(userRepository.getUser("jessica")).thenReturn(User.builder().username("jessica").password("j12345678").build());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "roseanne")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "roseanne")
                .header("X-Password", "j12345678"))
                .andExpect(status().isNotFound());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "peter")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + REQUEST)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "jessica")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    @DisplayName("Accept friend requests")
    void acceptFriendshipRequest() throws Exception {
        User johnDoe = User.builder().username("johndoe").password("j12345678").build();
        User roseanne = User.builder().username("roseanne").password("r3456789").friend(johnDoe).build();
        when(userRepository.findUser("roseanne", "r3456789")).thenReturn(roseanne);
        when(userRepository.findUser("roseanne", "r3456780")).thenThrow(InvalidCredentialException.class);
        when(userRepository.getUser("johndoe")).thenReturn(johnDoe);
        when(userRepository.getUser("roseanne")).thenReturn(roseanne);
        when(userRepository.getUser("peter")).thenReturn(User.builder().username("peter").password("j12345678").build());
        when(friendshipRepository.acceptFriendship(roseanne, johnDoe))
                .thenReturn(FriendshipRequest.builder()
                        .userFrom(roseanne)
                        .userTo(johnDoe)
                        .status(ACCEPTED)
                        .build())
                .thenThrow(AlreadyExistsException.class);

        when(userRepository.addFriend(roseanne, johnDoe)).thenReturn(roseanne);

        mockMvc.perform(post(FRIENDSHIP_MAPPING + ACCEPT)
                .param("usernameFrom", "roseanne")
                .param("usernameTo", "johndoe")
                .header("X-Password", "r3456789"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + ACCEPT)
                .param("usernameFrom", "roseanne")
                .param("usernameTo", "johndoe")
                .header("X-Password", "r3456789"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + ACCEPT)
                .param("usernameFrom", "roseanne")
                .param("usernameTo", "johndoe")
                .header("X-Password", "r3456780"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + ACCEPT)
                .param("usernameFrom", "roseanne")
                .param("usernameTo", "peter")
                .header("X-Password", "r3456789"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Decline friendship request")
    void declineFriendshipRequest() throws Exception {
        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param("usernameFrom", "peter")
                .param("usernameTo", "johndoe")
                .header("X-Password", "p4567890"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param("usernameFrom", "peter")
                .param("usernameTo", "johndoe")
                .header("X-Password", "p4567890"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param("usernameFrom", "peter")
                .param("usernameTo", "jessica")
                .header("X-Password", "p4567891"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param("usernameFrom", "peter")
                .param("usernameTo", "jessica")
                .header("X-Password", "p4567890"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("other scenarios")
    void friendshipRequest() throws Exception {
        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "robert")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param("usernameFrom", "robert")
                .param("usernameTo", "johndoe")
                .header("X-Password", "r0123456"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "robert")
                .header("X-Password", "j12345678"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param("usernameFrom", "robert")
                .param("usernameTo", "johndoe")
                .header("X-Password", "r0123456"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(FRIENDSHIP_MAPPING + DECLINE)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "robert")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("list friends test cases")
    void listFriends() throws Exception {
        final User roseanne = User.builder().username("roseanne").build();
        final User robert = User.builder().username("robert").build();
        final User johndoe = User.builder().username("johndoe").build();

        when(userRepository.findUser("johndoe", "j12345678")).thenReturn(User.builder().username("johndoe").password("j12345678").build());
        when(userRepository.findUser("johndoe", "j12345679")).thenThrow(new InvalidCredentialException("Invalid credentials for John Doe"));
        when(userRepository.findUser("samantha", "x12345678")).thenThrow(new InvalidCredentialException("Samantha doesn't exists"));
        when(userRepository.findUser("roseanne", "r3456789")).thenReturn(User.builder().username("johndoe").password("j12345678").build());
        when(userRepository.findUser("robert", "r3456789")).thenReturn(User.builder().username("johndoe").password("j12345678").build());
        when(userRepository.findUser("peter", "p4567890")).thenReturn(User.builder().username("johndoe").password("j12345678").build());
        when(userRepository.getUser("samantha")).thenThrow(new NotFoundException("Anyul"));

        when(userRepository.getUser("johndoe"))
                .thenReturn(User.builder().username("johndoe")
                        .friend(roseanne)
                        .friend(robert)
                        .build());

        when(userRepository.getUser("roseanne"))
                .thenReturn(User.builder().username("roseanne").friend(johndoe).build());

        when(userRepository.getUser("robert"))
                .thenReturn(User.builder().username("robert").friend(johndoe).build());

        when(userRepository.getUser("peter")).thenReturn(johndoe);

        mockMvc.perform(get(FRIENDSHIP_MAPPING + LIST)
                .param("username", "johndoe")
                .header("X-Password", "j12345679"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get(FRIENDSHIP_MAPPING + LIST)
                .param("username", "samantha")
                .header("X-Password", "x12345678"))
                .andExpect(status().isUnauthorized());

        testFindFriends("johndoe", "j12345678", "[\"roseanne\",\"robert\"]");

        testFindFriends("roseanne", "r3456789", "[\"johndoe\"]");

        testFindFriends("robert", "r3456789", "[\"johndoe\"]");

        testFindFriends("peter", "p4567890", "[]");
    }

    private void testFindFriends(String user, String password, String result) throws Exception {
        final String response4 = mockMvc.perform(get(FRIENDSHIP_MAPPING + LIST)
                .param("username", user)
                .header("X-Password", password))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn().getResponse().getContentAsString();
        assertThat(response4).isEqualTo(result);
    }
}