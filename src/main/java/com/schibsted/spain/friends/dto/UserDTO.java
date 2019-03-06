package com.schibsted.spain.friends.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDTO {

    private String username;
    private String password;

    private List<UserDTO> friends;
}
