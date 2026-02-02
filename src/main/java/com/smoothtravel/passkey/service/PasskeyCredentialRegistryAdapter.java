package com.smoothtravel.passkey.service;

import com.smoothtravel.passkey.repository.PasskeyCredentialRepository;
import com.smoothtravel.user.entity.User;
import com.smoothtravel.user.repository.UserRepository;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class PasskeyCredentialRegistryAdapter implements CredentialRepository {

    @Inject
    PasskeyCredentialRepository passkeyCredentialRepository;

    @Inject
    UserRepository userRepository;

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            return Collections.emptySet();
        }
        return passkeyCredentialRepository.findByUserId(user.get().id).stream()
                .map(cred -> PublicKeyCredentialDescriptor.builder()
                        .id(new ByteArray(cred.credentialId))
                        .build())
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        return userRepository.findByEmail(username)
                .map(user -> new ByteArray(uuidToBytes(user.id)));
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        java.util.UUID userId = bytesToUuid(userHandle.getBytes());
        return userRepository.findByIdOptional(userId)
                .map(user -> user.email);
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        return passkeyCredentialRepository.findByCredentialId(credentialId.getBytes())
                .map(cred -> RegisteredCredential.builder()
                        .credentialId(new ByteArray(cred.credentialId))
                        .userHandle(new ByteArray(uuidToBytes(cred.user.id)))
                        .publicKeyCose(new ByteArray(cred.publicKeyCose))
                        .signatureCount(cred.signatureCount)
                        .build());
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        return passkeyCredentialRepository.findByCredentialId(credentialId.getBytes())
                .map(cred -> RegisteredCredential.builder()
                        .credentialId(new ByteArray(cred.credentialId))
                        .userHandle(new ByteArray(uuidToBytes(cred.user.id)))
                        .publicKeyCose(new ByteArray(cred.publicKeyCose))
                        .signatureCount(cred.signatureCount)
                        .build())
                .map(Set::of)
                .orElse(Collections.emptySet());
    }

    static byte[] uuidToBytes(java.util.UUID uuid) {
        java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(16);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    static java.util.UUID bytesToUuid(byte[] bytes) {
        java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(bytes);
        return new java.util.UUID(bb.getLong(), bb.getLong());
    }
}
