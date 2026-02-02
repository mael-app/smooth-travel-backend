package com.smoothtravel.passkey.repository;

import com.smoothtravel.passkey.entity.PasskeyCredential;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PasskeyCredentialRepository implements PanacheRepositoryBase<PasskeyCredential, UUID> {

    public List<PasskeyCredential> findByUserId(UUID userId) {
        return list("user.id", userId);
    }

    public Optional<PasskeyCredential> findByCredentialId(byte[] credentialId) {
        return find("credentialId", (Object) credentialId).firstResultOptional();
    }
}
