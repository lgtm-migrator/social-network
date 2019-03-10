package com.schibsted.spain.friends.service;

import com.schibsted.spain.friends.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    UserDTO signup(String username, String password);

    UserDTO addFriend(String username, String username2);

    UserDTO authenticate(String username, String password);
}
