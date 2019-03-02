package com.schibsted.spain.friends.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Builder
@Getter
@Setter
public class User {
    private String username;
    private String password;

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof User))
            return false;
        if (obj == this)
            return true;
        return this.getUsername().equals(((User) obj).getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
