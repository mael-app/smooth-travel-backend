package com.smoothtravel.passkey.service;

import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Set;

@ApplicationScoped
public class WebAuthnConfig {

    @ConfigProperty(name = "app.webauthn.rp-id")
    String rpId;

    @ConfigProperty(name = "app.webauthn.rp-name")
    String rpName;

    @ConfigProperty(name = "app.webauthn.origin")
    String origin;

    @Inject
    PasskeyCredentialRegistryAdapter credentialRepository;

    @Produces
    @ApplicationScoped
    public RelyingParty relyingParty() {
        RelyingPartyIdentity identity = RelyingPartyIdentity.builder()
                .id(rpId)
                .name(rpName)
                .build();

        return RelyingParty.builder()
                .identity(identity)
                .credentialRepository(credentialRepository)
                .origins(Set.of(origin))
                .build();
    }
}
