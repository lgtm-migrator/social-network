package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.service.FriendshipService;
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
}