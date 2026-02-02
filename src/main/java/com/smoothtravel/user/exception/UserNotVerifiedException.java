package com.smoothtravel.user.exception;

public class UserNotVerifiedException extends RuntimeException {

    public UserNotVerifiedException(String email) {
        super("The account with email '" + email + "' is not verified");
    }
}
