package com.smoothtravel.user.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String email) {
        super("No account found with email '" + email + "'");
    }
}
