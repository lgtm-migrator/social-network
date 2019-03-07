package com.schibsted.spain.friends.entity;

import com.schibsted.spain.friends.dto.UserDTO;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    private String username;
    private String password;

    @Singular
    private Set<User> friends;

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

    @Override
    public String toString() {
        return String.format("User: %s, password:%s, friends: %d", username, password, friends.size());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * creates a data transfer object from an entity
     *
     * @return a new {@link UserDTO} transfer object
     */
    public UserDTO toDto() {
        return UserDTO.builder()
                .username(this.username)
                .password(this.password)
                .friends(this.friends.stream().map(User::toDto).collect(Collectors.toList()))
                .build();
    }

    /**
     * Creates a new entity from a data transfer object
     *
     * @param userDTO {@link UserDTO} transfer object
     */
    public void fromDTO(UserDTO userDTO) {
        this.username = userDTO.getUsername();
        this.password = userDTO.getPassword();
        this.friends = userDTO.getFriends().stream().map(userDTO1 -> {
            User user = User.builder().build();
            user.fromDTO(userDTO1);
            return user;

        }).collect(Collectors.toSet());
    }
}
