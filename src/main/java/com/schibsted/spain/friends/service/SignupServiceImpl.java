package com.schibsted.spain.friends.service;

import com.google.common.base.Preconditions;

public class SignupServiceImpl implements SignupService {
    /**
     * User signup
     *
     * @param username username
     * @param password password
     * @return true if valid, false if invalid
     */
    @Override
    public Boolean signup(String username, String password) {
        Preconditions.checkNotNull(username, "Username cannot be null");
        Preconditions.checkNotNull(password, "Password cannot be null");
        Preconditions.checkArgument(username.length() >= 5 && username.length() <= 10 && username.matches("[a-zA-Z0-9]+"), "Invalid format: username");
        Preconditions.checkArgument(password.length() >= 8 && password.length() <= 12 && password.matches("[a-zA-Z0-9]+"), "Invalid format: password");
        return true;
    }
}
