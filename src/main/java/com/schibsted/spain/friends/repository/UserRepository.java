package com.schibsted.spain.friends.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    Boolean addUser(String username, String password);
}
