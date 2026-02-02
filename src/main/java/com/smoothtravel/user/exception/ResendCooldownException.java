package com.smoothtravel.user.exception;

public class ResendCooldownException extends RuntimeException {

    public ResendCooldownException() {
        super("Please wait at least 30 seconds before requesting a new code");
    }
}
