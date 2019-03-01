package com.schibsted.spain.friends.service;

import org.springframework.stereotype.Service;

@Service
public interface SignupService {

    Boolean signup(String username, String password);
}
