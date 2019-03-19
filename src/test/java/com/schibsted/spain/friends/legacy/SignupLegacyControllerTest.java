package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.repository.FriendshipRepository;
import com.schibsted.spain.friends.service.FriendshipService;
import com.schibsted.spain.friends.service.UserService;
import com.schibsted.spain.friends.utils.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.schibsted.spain.friends.utils.Utils.SIGN_UP_MAPPING;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class SignupLegacyControllerTest {

    private static final String JOHNDOE = "johndoe";
    private static final String PASSWORD = "j12345678";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    FriendshipService friendshipService;

    @MockBean
    FriendshipRepository friendshipRepository;

    @Test
    @DisplayName("Signup with invalid data")
    void signupErrorTestCases() throws Exception {
        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param(Utils.USERNAME, "john_doe")
                .header(Utils.X_PASS, PASSWORD))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param(Utils.USERNAME, JOHNDOE)
                .header(Utils.X_PASS, "j12-45678"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param(Utils.USERNAME, JOHNDOE)
                .header(Utils.X_PASS, "j1234"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param(Utils.USERNAME, JOHNDOE)
                .header(Utils.X_PASS, "j1234567890123"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param(Utils.USERNAME, "johnisaniceguy")
                .header(Utils.X_PASS, PASSWORD))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Signup with valid data")
    void signupWithValidData() throws Exception {
        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param(Utils.USERNAME, JOHNDOE)
                .header(Utils.X_PASS, PASSWORD))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param(Utils.USERNAME, JOHNDOE)
                .header(Utils.X_PASS, PASSWORD))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param(Utils.USERNAME, "roseanne")
                .header(Utils.X_PASS, "r3456789"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param(Utils.USERNAME, "peter")
                .header(Utils.X_PASS, "p4567890"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param(Utils.USERNAME, "jessica")
                .header(Utils.X_PASS, "j5678901"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param(Utils.USERNAME, "robert")
                .header(Utils.X_PASS, "r0123456"))
                .andExpect(status().is2xxSuccessful());
    }
}