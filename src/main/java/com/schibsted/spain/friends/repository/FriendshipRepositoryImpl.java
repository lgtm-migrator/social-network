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

    @Override
    public FriendshipRequest requestFriendship(User requesterUser, User requestedUser) {

        final boolean hasNoPendingOrAcceptedRequest = friendshipRequests.stream()
                .noneMatch(request ->
                        isRequest(request, requestedUser, requesterUser, PENDING) ||
                                isRequest(request, requesterUser, requestedUser, PENDING) ||
                                isRequest(request, requestedUser, requesterUser, ACCEPTED) ||
                                isRequest(request, requesterUser, requestedUser, ACCEPTED));

        if (hasNoPendingOrAcceptedRequest) {
            final FriendshipRequest friendshipRequest = FriendshipRequest.builder()
                    .requestedUser(requestedUser)
                    .requesterUser(requesterUser)
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

    private boolean isRequest(FriendshipRequest request, User userFrom, User userTo, FriendRequestStatus status) {
        return request.getRequestedUser().getUsername().equalsIgnoreCase(userFrom.getUsername()) &&
                request.getRequesterUser().getUsername().equalsIgnoreCase(userTo.getUsername()) &&
                request.getStatus().equals(status);
    }

    @Override
    public FriendshipRequest acceptFriendship(User requester, User requested) {
        final Predicate<FriendshipRequest> acceptedRequestPredicate = request -> isRequest(request, requester, requested, ACCEPTED) || isRequest(request, requested, requester, ACCEPTED);
        final boolean hasAcceptedRequests = friendshipRequests.stream()
                .anyMatch(acceptedRequestPredicate);
        final boolean hasPendingRequests = friendshipRequests.stream()
                .anyMatch(request -> isRequest(request, requester, requested, PENDING) || isRequest(request, requested, requester, PENDING));

        if (hasAcceptedRequests) {
            throw new AlreadyExistsException(String.format("A friend request from %s has been already accepted by %s", requester.getUsername(), requester.getUsername()));
        }

        if (hasPendingRequests) {
            friendshipRequests.stream()
                    .filter(request -> isRequest(request, requester, requested, PENDING) || isRequest(request, requested, requester, PENDING))
                    .forEach(request -> request.setStatus(ACCEPTED));
        } else {
            throw new NotFoundException(String.format("No pending requests between %s and %s", requester, requested));
        }
        return friendshipRequests.stream().filter(acceptedRequestPredicate).findFirst().orElse(null);
    }

    @Override
    public FriendshipRequest declineFriendship(User requester, User requested) {
        final Predicate<FriendshipRequest> declinedRequestPredicate = request -> isRequest(request, requester, requested, DECLINED) || isRequest(request, requested, requester, DECLINED);
        final boolean hasPendingRequests = friendshipRequests.stream().anyMatch(request -> isRequest(request, requester, requested, PENDING));

        if (hasPendingRequests) {
            friendshipRequests.stream()
                    .filter(request -> isRequest(request, requester, requested, PENDING) || isRequest(request, requested, requester, PENDING))
                    .forEach(request -> request.setStatus(DECLINED));
        } else {
            throw new NotFoundException(String.format("No pending requests between %s and %s", requester, requested));
        }

        return friendshipRequests.stream().filter(declinedRequestPredicate).findFirst().orElse(null);
    }
}
