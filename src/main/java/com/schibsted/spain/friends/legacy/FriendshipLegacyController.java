package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.service.FriendshipService;
import com.schibsted.spain.friends.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friendship")
@Api(value = "Friendship", tags = {"Friendship controller"})
public class FriendshipLegacyController {

    private final FriendshipService friendshipService;

    private final UserService userService;

    public FriendshipLegacyController(@Autowired FriendshipService friendshipService, @Autowired UserService userService) {
        this.friendshipService = friendshipService;
        this.userService = userService;
    }

    @PostMapping("/request")
    @ApiOperation(nickname = "request friendship", value = "Request friendship")
    Boolean requestFriendship(@RequestParam("usernameFrom") String usernameFrom,
                              @RequestParam("usernameTo") String usernameTo,
                              @RequestHeader("X-Password") String password) {
        userService.authenticate(usernameFrom, password);
        return friendshipService.requestFriendShip(usernameFrom, usernameTo);
    }

    @PostMapping("/accept")
    @ApiOperation(value = "Accept friendship")
    Boolean acceptFriendship(@RequestParam("usernameFrom") String usernameFrom,
                             @RequestParam("usernameTo") String usernameTo,
                             @RequestHeader("X-Password") String password) {
        userService.authenticate(usernameFrom, password);
        return friendshipService.acceptFriendShip(usernameFrom, usernameTo);
    }

    @PostMapping("/decline")
    @ApiOperation(value = "Decline friendship")
    Boolean declineFriendship(@RequestParam("usernameFrom") String usernameFrom,
                              @RequestParam("usernameTo") String usernameTo,
                              @RequestHeader("X-Password") String password) {
        userService.authenticate(usernameFrom, password);
        return friendshipService.declineFriendShip(usernameFrom, usernameTo);
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List user's friends")
    Object listFriends(@RequestParam("username") String username,
                       @RequestHeader("X-Password") String password) {
        userService.authenticate(username, password);
        return friendshipService.listFriends(username);
    }
}
