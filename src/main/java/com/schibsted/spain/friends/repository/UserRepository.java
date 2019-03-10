package com.schibsted.spain.friends.repository;

import com.schibsted.spain.friends.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    User addUser(String username, String password);

    User getUser(String username);

    User findUser(String username, String password);

    User updateUser(User user);

    User addFriend(User user, User friend);
}
