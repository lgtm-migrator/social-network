package com.schibsted.spain.friends.repository;

import com.schibsted.spain.friends.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    Boolean addUser(String username, String password);

    User getUser(String username);

    User findUser(String username, String password);

    Boolean updateUser(User user);
}
