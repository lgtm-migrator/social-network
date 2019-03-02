package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.service.SignupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SignupLegacyController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SignupLegacyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignupService signupService;

    @Test
    @DisplayName("Login with wrong name")
    void loginWithWrongName() throws Exception {
        Mockito.when(signupService.signup(Mockito.anyString(), Mockito.anyString())).thenThrow(com.schibsted.spain.friends.utils.exceptions.ValidationException.class);

        mockMvc.perform(post("/signup")
                .param("username", "john_doe")
                .header("X-Password", "j12345678"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login with wrong name")
    void loginWithWrongPassword() throws Exception {
        mockMvc.perform(post("/signup")
                .param("username", "johndoe")
                .header("X-Password", "j12-45678"))
                .andExpect(status().isBadRequest());
    }
}