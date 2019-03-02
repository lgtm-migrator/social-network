package com.schibsted.spain.friends.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryImplTest {

    private UserRepositoryImpl userRepository = new UserRepositoryImpl();

    @Test
    @DisplayName("should insert unique users by their username")
    void addUser() {
        assertThat(userRepository.addUser("user", "1234")).isTrue();
        assertThat(userRepository.addUser("user", "1234")).isFalse();
    }
}