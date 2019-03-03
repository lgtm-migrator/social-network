package com.schibsted.spain.friends.service;

import org.springframework.stereotype.Service;

@Service
public interface UserService {

    Boolean signup(String username, String password);

    Boolean addFriend(String username, String username2);

    Boolean authenticate(String username, String password);
}
