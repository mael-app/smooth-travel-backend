package com.smoothtravel.user.exception;

public class AlreadyVerifiedException extends RuntimeException {

    public AlreadyVerifiedException(String email) {
        super("The account with email '" + email + "' is already verified");
    }
}
