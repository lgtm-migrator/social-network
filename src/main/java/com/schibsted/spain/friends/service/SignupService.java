package com.schibsted.spain.friends.service;

import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;

@Service
public interface SignupService {

    Boolean signup(String username, String password) throws ValidationException;
}
