package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.service.FriendshipService;
import com.schibsted.spain.friends.utils.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class SignupLegacyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    FriendshipService friendshipService;

    @Test
    @DisplayName("Signup with invalid data")
    void signupErrorTestCases() throws Exception {
        mockMvc.perform(post("/signup")
                .param("username", "john_doe")
                .header("X-Password", "j12345678"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/signup")
                .param("username", "johndoe")
                .header("X-Password", "j12-45678"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/signup")
                .param("username", "johndoe")
                .header("X-Password", "j1234"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/signup")
                .param("username", "johndoe")
                .header("X-Password", "j1234567890123"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/signup")
                .param("username", "johnisaniceguy")
                .header("X-Password", "j12345678"))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Signup with valid data")
    void signupWithValidData() throws Exception {
        mockMvc.perform(post("/signup")
                .param("username", "johndoe")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post("/signup")
                .param("username", "johndoe")
                .header("X-Password", "j12345678"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/signup")
                .param("username", "roseanne")
                .header("X-Password", "r3456789"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post("/signup")
                .param("username", "peter")
                .header("X-Password", "p4567890"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post("/signup")
                .param("username", "jessica")
                .header("X-Password", "j5678901"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post("/signup")
                .param("username", "robert")
                .header("X-Password", "r0123456"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void simpleAssert() {
        Boolean falseable = false;
        assertThat(falseable).isFalse();
    }

    @Test
    @DisplayName("Friendship request test cases")
    void friendshipRequestErrorTestCases() throws Exception {
        mockMvc.perform(post(Utils.FRIENDSHIP_REQUEST_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "samantha")
                .header("X-Password", "j12345679"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(Utils.FRIENDSHIP_REQUEST_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "samantha")
                .header("X-Password", "j12345678"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(Utils.FRIENDSHIP_REQUEST_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "roseanne")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(Utils.FRIENDSHIP_REQUEST_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "roseanne")
                .header("X-Password", "j12345678"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(Utils.FRIENDSHIP_REQUEST_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "peter")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(Utils.FRIENDSHIP_REQUEST_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "jessica")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("successfull test cases")
    void friendshipRequestSuccessfulTestCases() throws Exception {
        mockMvc.perform(post(Utils.FRIENDSHIP_REQUEST_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "roseanne")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(Utils.FRIENDSHIP_REQUEST_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "roseanne")
                .header("X-Password", "j12345678"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(Utils.FRIENDSHIP_REQUEST_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "peter")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(Utils.FRIENDSHIP_REQUEST_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "jessica")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    @DisplayName("Accept friend requests")
    void acceptFriendshipRequest() throws Exception {
        friendshipService.requestFriendShip("roseanne", "johndoe");
        mockMvc.perform(post(Utils.FRIENDSHIP_ACCEPT_URL)
                .param("usernameFrom", "roseanne")
                .param("usernameTo", "johndoe")
                .header("X-Password", "r3456789"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(Utils.FRIENDSHIP_ACCEPT_URL)
                .param("usernameFrom", "roseanne")
                .param("usernameTo", "johndoe")
                .header("X-Password", "r3456789"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(Utils.FRIENDSHIP_ACCEPT_URL)
                .param("usernameFrom", "roseanne")
                .param("usernameTo", "johndoe")
                .header("X-Password", "r3456780"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(Utils.FRIENDSHIP_ACCEPT_URL)
                .param("usernameFrom", "roseanne")
                .param("usernameTo", "peter")
                .header("X-Password", "r3456789"))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Decline friendship request")
    void declineFriendshipRequest() throws Exception {
        mockMvc.perform(post(Utils.FRIENDSHIP_DECLINE_URL)
                .param("usernameFrom", "peter")
                .param("usernameTo", "johndoe")
                .header("X-Password", "p4567890"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(Utils.FRIENDSHIP_DECLINE_URL)
                .param("usernameFrom", "peter")
                .param("usernameTo", "johndoe")
                .header("X-Password", "p4567890"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(Utils.FRIENDSHIP_DECLINE_URL)
                .param("usernameFrom", "peter")
                .param("usernameTo", "jessica")
                .header("X-Password", "p4567891"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(Utils.FRIENDSHIP_DECLINE_URL)
                .param("usernameFrom", "peter")
                .param("usernameTo", "jessica")
                .header("X-Password", "p4567890"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("other scenarios")
    void friendshipRequest() throws Exception {
        mockMvc.perform(post(Utils.FRIENDSHIP_DECLINE_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "robert")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(Utils.FRIENDSHIP_DECLINE_URL)
                .param("usernameFrom", "robert")
                .param("usernameTo", "johndoe")
                .header("X-Password", "r0123456"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(Utils.FRIENDSHIP_DECLINE_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "robert")
                .header("X-Password", "j12345678"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(Utils.FRIENDSHIP_DECLINE_URL)
                .param("usernameFrom", "robert")
                .param("usernameTo", "johndoe")
                .header("X-Password", "r0123456"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(Utils.FRIENDSHIP_DECLINE_URL)
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "robert")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("list friends test cases")
    void listFriends() throws Exception {
        mockMvc.perform(post(Utils.FRIENDSHIP_LIST_URL)
                .param("username", "johndoe")
                .header("X-Password", "j12345679"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(Utils.FRIENDSHIP_LIST_URL)
                .param("username", "samantha")
                .header("X-Password", "x12345678"))
                .andExpect(status().isBadRequest());

        testFindFriends("johndoe", "j12345678", "[\"roseanne\",\"robert\"]");

        testFindFriends("roseanne", "r3456789", "[\"johndoe\"]");

        testFindFriends("robert", "r3456789", "[\"johndoe\"]");

        testFindFriends("peter", "p4567890", "[]");
    }

    private void testFindFriends(String user, String password, String result) throws Exception {
        final String response4 = mockMvc.perform(post(Utils.FRIENDSHIP_LIST_URL)
                .param("username", user)
                .header("X-Password", password))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getContentAsString();
        assertThat(response4).isEqualTo(result);
    }
}