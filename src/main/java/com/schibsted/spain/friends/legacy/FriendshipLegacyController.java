package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.dto.FriendshipRequestDTO;
import com.schibsted.spain.friends.service.FriendshipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    ResponseEntity<FriendshipRequestDTO> requestFriendship(
            @RequestParam(USERNAME_FROM) String usernameFrom,
            @RequestParam(USERNAME_TO) String usernameTo,
            @RequestHeader(X_PASS) String password) {
        return ResponseEntity.ok(friendshipService.requestFriendShip(usernameFrom, usernameTo));
    }

    @PostMapping("/accept")
    @ApiOperation(value = "Accept friendship")
    ResponseEntity<FriendshipRequestDTO> acceptFriendship(@RequestParam(USERNAME_FROM) String usernameFrom,
                                                          @RequestParam(USERNAME_TO) String usernameTo,
                                                          @RequestHeader(X_PASS) String password) {
        return ResponseEntity.ok(friendshipService.acceptFriendShip(usernameFrom, usernameTo));
    }

    @PostMapping("/decline")
    @ApiOperation(value = "Decline friendship")
    ResponseEntity<FriendshipRequestDTO> declineFriendship(@RequestParam(USERNAME_FROM) String usernameFrom,
                                                           @RequestParam(USERNAME_TO) String usernameTo,
                                                           @RequestHeader(X_PASS) String password) {
        return ResponseEntity.ok(friendshipService.declineFriendShip(usernameFrom, usernameTo));
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List user's friends")
    ResponseEntity<List<String>> listFriends(@RequestParam(USERNAME) String username,
                                             @RequestHeader(X_PASS) String password) {

        final List<String> friends = friendshipService.listFriends(username);
        return ResponseEntity.ok(friends);
    }
}
