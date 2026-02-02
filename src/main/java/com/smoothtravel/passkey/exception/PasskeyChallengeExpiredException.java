package com.smoothtravel.passkey.exception;

public class PasskeyChallengeExpiredException extends RuntimeException {

    public PasskeyChallengeExpiredException() {
        super("Passkey challenge has expired, please start again");
    }
}
