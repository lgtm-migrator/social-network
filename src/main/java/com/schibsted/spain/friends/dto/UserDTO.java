package com.schibsted.spain.friends.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "User", description = "User model. Used in signup and friend requests")
public class UserDTO {

    private String username;
    @JsonIgnore
    private String password;

    @Singular
    private List<UserDTO> friends;
}
