package com.schibsted.spain.friends.repository;

import com.schibsted.spain.friends.entity.FriendRequestStatus;
import com.schibsted.spain.friends.entity.FriendshipRequest;
import com.schibsted.spain.friends.entity.User;
import com.schibsted.spain.friends.utils.exceptions.AlreadyExistsException;
import com.schibsted.spain.friends.utils.exceptions.ErrorDto;
import com.schibsted.spain.friends.utils.exceptions.NotFoundException;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static com.schibsted.spain.friends.entity.FriendRequestStatus.*;

@Repository
public class FriendshipRepositoryImpl implements FriendshipRepository {

    private Set<FriendshipRequest> friendshipRequests = new HashSet<>();

    /**
     * Creates a friendship request if there isn't already  a pending one
     *
     * @param requesterUser requester user
     * @param requestedUser requested user
     * @return the friend request
     */
    @Override
    public FriendshipRequest requestFriendship(User requesterUser, User requestedUser) {

        final boolean hasNoPendingOrAcceptedRequest = friendshipRequests.stream()
                .noneMatch(request ->
                        isMutualRequest(request, requestedUser, requesterUser, PENDING) ||
                                isMutualRequest(request, requestedUser, requesterUser, ACCEPTED));

        if (hasNoPendingOrAcceptedRequest) {
            final FriendshipRequest friendshipRequest = FriendshipRequest.builder()
                    .userTo(requestedUser)
                    .userFrom(requesterUser)
                    .status(PENDING)
                    .build();
            friendshipRequests.add(friendshipRequest);
            return friendshipRequest;

        } else {
            throw new AlreadyExistsException(ErrorDto.builder()
                    .message(String.format("There is a pending request between %s and %s", requesterUser.getUsername(), requestedUser.getUsername()))
                    .exceptionClass(this.getClass().getSimpleName())
                    .build());
        }
    }

    private boolean isRequest(FriendshipRequest request, User sourceUser, User targetUser, FriendRequestStatus status) {
        return request.getUserTo().getUsername().equalsIgnoreCase(sourceUser.getUsername()) &&
                request.getUserFrom().getUsername().equalsIgnoreCase(targetUser.getUsername()) &&
                request.getStatus().equals(status);
    }

    private boolean isMutualRequest(FriendshipRequest request, User userFrom, User userTo, FriendRequestStatus status) {
        return isRequest(request, userFrom, userTo, status) || isRequest(request, userTo, userFrom, status);
    }

    /**
     * Accepts a pending friend request
     * @param requester requester user
     * @param requested requested user
     * @return the accepted friendship request
     */
    @Override
    public FriendshipRequest acceptFriendship(User requester, User requested) {
        final Predicate<FriendshipRequest> acceptedRequestPredicate = request -> isMutualRequest(request, requested, requester, ACCEPTED);
        final boolean hasAcceptedRequests = friendshipRequests.stream().anyMatch(acceptedRequestPredicate);
        final boolean hasPendingRequests = friendshipRequests.stream().anyMatch(request -> isMutualRequest(request, requester, requested, PENDING));

        if (hasAcceptedRequests) {
            throw new AlreadyExistsException(String.format("A friend request from %s has been already accepted by %s", requester.getUsername(), requester.getUsername()));
        }

        return modifyFriendshipRequest(requester, requested, acceptedRequestPredicate, hasPendingRequests, ACCEPTED);
    }

    private FriendshipRequest modifyFriendshipRequest(User requester, User requested, Predicate<FriendshipRequest> predicate, boolean condition, FriendRequestStatus status) {
        if (condition) {
            friendshipRequests.stream()
                    .filter(request -> isRequest(request, requester, requested, PENDING) || isRequest(request, requested, requester, PENDING))
                    .forEach(request -> request.setStatus(status));
        } else {
            throw new NotFoundException(String.format("No pending requests between %s and %s", requester, requested));
        }
        return friendshipRequests.stream().filter(predicate).findFirst().orElse(null);
    }

    /**
     * Declines a pending friendship request
     * @param requester requester user
     * @param requested requested user
     * @return the declined pull request
     */
    @Override
    public FriendshipRequest declineFriendship(User requester, User requested) {
        final Predicate<FriendshipRequest> declinedRequestPredicate = request -> isMutualRequest(request, requester, requested, DECLINED);
        final boolean hasPendingRequests = friendshipRequests.stream().anyMatch(request -> isMutualRequest(request, requester, requested, PENDING));

        return modifyFriendshipRequest(requester, requested, declinedRequestPredicate, hasPendingRequests, DECLINED);
    }
}
