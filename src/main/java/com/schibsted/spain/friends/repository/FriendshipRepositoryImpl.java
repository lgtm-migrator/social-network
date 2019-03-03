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

import static com.schibsted.spain.friends.entity.FriendRequestStatus.*;

@Repository
public class FriendshipRepositoryImpl implements FriendshipRepository {

    private Set<FriendshipRequest> friendshipRequests = new HashSet<>();

    @Override
    public Boolean requestFriendship(User requesterUser, User requestedUser) {

        final boolean noPendingRequestMatch = friendshipRequests.stream()
                .noneMatch(request ->
                        isRequest(request, requestedUser, requesterUser, PENDING) ||
                                isRequest(request, requesterUser, requestedUser, PENDING) ||
                                isRequest(request, requestedUser, requesterUser, ACCEPTED) ||
                                isRequest(request, requesterUser, requestedUser, ACCEPTED));

        if (noPendingRequestMatch) {
            return friendshipRequests.add(FriendshipRequest.builder()
                    .requestedUser(requestedUser)
                    .requesterUser(requesterUser)
                    .status(PENDING)
                    .build());

        } else {
            throw new AlreadyExistsException(ErrorDto.builder()
                    .msg(String.format("There is a pending request between %s and %s", requesterUser.getUsername(), requestedUser.getUsername()))
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
    public Boolean acceptFriendship(User requester, User requested) {
        final boolean hasAcceptedRequests = friendshipRequests.stream().anyMatch(request -> isRequest(request, requester, requested, ACCEPTED) ||
                isRequest(request, requested, requester, ACCEPTED));
        final boolean hasPendingRequests = friendshipRequests.stream().anyMatch(request -> isRequest(request, requester, requested, PENDING) ||
                isRequest(request, requested, requester, PENDING));

        if (hasAcceptedRequests) {
            throw new AlreadyExistsException(String.format("A friend request from %s has been already accepted by %s", requester, requester));
        }

        if (hasPendingRequests) {
            friendshipRequests.stream()
                    .filter(request -> isRequest(request, requester, requested, PENDING) ||
                            isRequest(request, requested, requester, PENDING)
                    )
                    .forEach(request -> request.setStatus(ACCEPTED));
        } else {
            throw new NotFoundException(String.format("No pending requests between %s and %s", requester, requested));
        }
        return true;
    }

    @Override
    public Boolean declineFriendship(User requester, User requested) {
        final boolean hasPendingRequests = friendshipRequests.stream().anyMatch(request -> isRequest(request, requester, requested, PENDING));

        if (hasPendingRequests) {
            friendshipRequests.stream()
                    .filter(request -> isRequest(request, requested, requested, PENDING))
                    .forEach(request -> request.setStatus(DECLINED));
        } else {
            throw new NotFoundException(String.format("No pending requests between %s and %s", requester, requested));
        }

        return false;
    }
}
