package com.schibsted.spain.friends.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class UserDTO {

    private String username;
    private String password;

    @Singular
    private List<UserDTO> friends;
}
