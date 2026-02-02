package com.smoothtravel.user.exception;

public class VerificationPendingException extends RuntimeException {

    public VerificationPendingException(String email) {
        super("A verification code has already been sent to '" + email + "'");
    }
}
