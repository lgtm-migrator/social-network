package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.service.SignupService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class FriendshipLegacyControllerTest {

    @Autowired
    SignupService signupService;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    void setupUsers() {
        signupService.signup("johndoe", "12345678");
        signupService.signup("roseanne", "12345678");
        signupService.signup("peter", "12345678");
    }

    @Test
    @DisplayName("Friendship request test cases")
    void friendshipRequestErrorTestCases() throws Exception {
        mockMvc.perform(post("/friendship/request")
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "samantha")
                .header("X-Password", "j12345679"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/friendship/request")
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "samantha")
                .header("X-Password", "j12345678"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/friendship/request")
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "roseanne")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post("/friendship/request")
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "roseanne")
                .header("X-Password", "j12345678"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/friendship/request")
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "peter")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post("/friendship/request")
                .param("usernameFrom", "johndoe")
                .param("usernameTo", "jessica")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());
    }
}