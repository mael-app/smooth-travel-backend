package com.smoothtravel.passkey.service;

import com.yubico.webauthn.RelyingParty;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class WebAuthnConfigTest {

    @Inject
    WebAuthnConfig webAuthnConfig;

    @Test
    void shouldCreateRelyingPartyBean() {
        RelyingParty relyingParty = webAuthnConfig.relyingParty();

        assertNotNull(relyingParty);
        assertNotNull(relyingParty.getIdentity());
    }
}
