package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.service.FriendshipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friendship")
@Api(value = "Friendship", tags = {"Friendship controller"})
public class FriendshipLegacyController {

    private final FriendshipService friendshipService;

    public FriendshipLegacyController(@Autowired FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @PostMapping("/request")
    @ApiOperation(nickname = "request friendship", value = "Request friendship")
    Boolean requestFriendship(@RequestParam("usernameFrom") String usernameFrom, @RequestParam("usernameTo") String usernameTo, @RequestHeader("X-Password") String password) {
        return friendshipService.requestFriendShip(usernameFrom, usernameTo);
    }

    @PostMapping("/accept")
    @ApiOperation(value = "Accept friendship")
    Boolean acceptFriendship(@RequestParam("usernameFrom") String usernameFrom, @RequestParam("usernameTo") String usernameTo, @RequestHeader("X-Password") String password) {
        return friendshipService.acceptFriendShip(usernameFrom, usernameTo);
    }

    @PostMapping("/decline")
    @ApiOperation(value = "Decline friendship")
    Boolean declineFriendship(@RequestParam("usernameFrom") String usernameFrom, @RequestParam("usernameTo") String usernameTo, @RequestHeader("X-Password") String password
    ) {
        return friendshipService.declineFriendShip(usernameFrom, usernameTo);
    }

    @GetMapping("/list")
    @ApiOperation(value = "List user's friends")
    Object listFriends(@RequestParam("username") String username, @RequestHeader("X-Password") String password) {
        return friendshipService.listFriends(username);
    }
}
