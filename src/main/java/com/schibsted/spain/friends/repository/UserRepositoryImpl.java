package com.schibsted.spain.friends.repository;

import com.schibsted.spain.friends.entity.User;
import com.schibsted.spain.friends.utils.exceptions.AlreadyExistsException;
import com.schibsted.spain.friends.utils.exceptions.ErrorDto;
import com.schibsted.spain.friends.utils.exceptions.InvalidCredentialException;
import com.schibsted.spain.friends.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private Set<User> users = new LinkedHashSet<>();

    /**
     * Add a user
     *
     * @param username username
     * @param password password
     * @return creates a user and adds it to the users list repository
     */
    public User addUser(String username, String password) {
        final boolean userExists = users.stream().anyMatch(user -> user.getUsername().equalsIgnoreCase(username) && user.getPassword().equalsIgnoreCase(password));
        // should I encrypt the user's password?
        final User user = User.builder().username(username).password(password).build();
        if (userExists) {
            throw new AlreadyExistsException(ErrorDto.builder()
                    .message(String.format("User %s already exists", username))
                    .exceptionClass(this.getClass().getSimpleName())
                    .build());
        }
        users.add(user);
        return user;
    }

    /**
     * Get a user by its username only
     *
     * @param username username
     * @return returns the first matched user {@link User} or a {@link NotFoundException}
     */
    @Override
    public User getUser(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    /**
     * Find a user by both the username and the password
     *
     * @param username username
     * @param password password
     * @return returns the first matched user {@link User} or a {@link InvalidCredentialException}
     */
    @Override
    public User findUser(String username, String password) {
        if (log.isDebugEnabled()) {
            log.debug("Users: {}", users);
        }
        return users.stream()
                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .orElseThrow(InvalidCredentialException::new);
    }

    /**
     * Updates a user on the users list, by removing it and adding it again with the updated value.
     * The remove statement works because the {@link User#equals(Object)} method compares only by name
     *
     * @param user former user
     * @return updated user
     */
    @Override
    public User updateUser(User user) {
        users.remove(user);
        users.add(user);
        return user;
    }

    /**
     * Picks an existing user and adds a friend and later on gets updated in the user list.
     *
     * @param user   user to add friend
     * @param friend a vararg of friend
     * @return the updated user
     */
    @Override
    public User addFriend(User user, User friend) {
        user.setFriends(mergeSet(user, friend));
        updateUser(user);

        friend.setFriends(mergeSet(friend, user));
        updateUser(friend);

        return user;
    }

    /**
     * Creates a new set from an existing set and a new element
     *
     * @param source   the source with the set of friends
     * @param target the new element to be added
     * @return a new set with both elements
     */
    private Set<User> mergeSet(User source, User target) {
        return Stream.of(
                Stream.of(target).collect(Collectors.toSet()),
                source.getFriends()
        )
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public Set<User> getUsers() {
        return users;
    }
}
