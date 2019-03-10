package com.schibsted.spain.friends.utils;

/**
 * Utility class for request mappings and params
 */
public final class Utils {

    public static final String FRIENDSHIP_LIST_URL = "/friendship/list";
    public static final String FRIENDSHIP_DECLINE_URL = "/friendship/decline";
    public static final String FRIENDSHIP_ACCEPT_URL = "friendship/accept";
    public static final String FRIENDSHIP_REQUEST_URL = "/friendship/request";
    public static final String USERNAME_FROM = "usernameFrom";
    public static final String USERNAME_TO = "usernameTo";
    public static final String X_PASS = "X-Password";
    public static final String USERNAME = "username";
    public static final String SIGN_UP_MAPPING = "/signup";
    public static final String FRIENDSHIP_MAPPING = "/friendship";
    public static final String REQUEST = "/request";
    public static final String ACCEPT = "/accept";
    public static final String DECLINE = "/decline";
    public static final String LIST = "/list";

    /**
     * utility classes should not be instantiated
     */
    private Utils() {
        //NOSONAR
    }
}