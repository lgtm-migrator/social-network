package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.service.FriendshipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.schibsted.spain.friends.utils.Utils.*;

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
    ResponseEntity requestFriendship(@RequestParam(USERNAME_FROM) String usernameFrom,
                                     @RequestParam(USERNAME_TO) String usernameTo,
                                     @RequestHeader(X_PASS) String password) {
        friendshipService.requestFriendShip(usernameFrom, usernameTo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept")
    @ApiOperation(value = "Accept friendship")
    ResponseEntity acceptFriendship(@RequestParam(USERNAME_FROM) String usernameFrom,
                                    @RequestParam(USERNAME_TO) String usernameTo,
                                    @RequestHeader(X_PASS) String password) {
        friendshipService.acceptFriendShip(usernameFrom, usernameTo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/decline")
    @ApiOperation(value = "Decline friendship")
    ResponseEntity declineFriendship(@RequestParam(USERNAME_FROM) String usernameFrom,
                                     @RequestParam(USERNAME_TO) String usernameTo,
                                     @RequestHeader(X_PASS) String password) {
        friendshipService.declineFriendShip(usernameFrom, usernameTo);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List user's friends")
    Object listFriends(@RequestParam(USERNAME) String username,
                       @RequestHeader(X_PASS) String password) {
        friendshipService.listFriends(username);
        return ResponseEntity.ok();
    }
}
