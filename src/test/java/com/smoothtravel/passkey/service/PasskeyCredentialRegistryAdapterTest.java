package com.smoothtravel.passkey.service;

import com.smoothtravel.passkey.entity.PasskeyCredential;
import com.smoothtravel.passkey.repository.PasskeyCredentialRepository;
import com.smoothtravel.user.entity.User;
import com.smoothtravel.user.repository.UserRepository;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.RegisteredCredential;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasskeyCredentialRegistryAdapterTest {

    @Mock
    PasskeyCredentialRepository passkeyCredentialRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    PasskeyCredentialRegistryAdapter adapter;

    private User user;
    private PasskeyCredential credential;

    @BeforeEach
    void setUp() {
        user = new User();
        user.id = UUID.randomUUID();
        user.email = "test@example.com";
        user.verified = true;
        user.createdAt = Instant.now();
        user.updatedAt = Instant.now();

        credential = new PasskeyCredential();
        credential.id = UUID.randomUUID();
        credential.user = user;
        credential.credentialId = new byte[]{1, 2, 3, 4};
        credential.publicKeyCose = new byte[]{5, 6, 7, 8};
        credential.signatureCount = 0;
        credential.discoverable = false;
        credential.createdAt = Instant.now();
        credential.updatedAt = Instant.now();
    }

    @Test
    void shouldReturnCredentialIdsForUsername() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passkeyCredentialRepository.findByUserId(user.id)).thenReturn(List.of(credential));

        Set<PublicKeyCredentialDescriptor> result = adapter.getCredentialIdsForUsername("test@example.com");

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnEmptyForUnknownUsername() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        Set<PublicKeyCredentialDescriptor> result = adapter.getCredentialIdsForUsername("unknown@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnUserHandleForUsername() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<ByteArray> result = adapter.getUserHandleForUsername("test@example.com");

        assertTrue(result.isPresent());
    }

    @Test
    void shouldReturnUsernameForUserHandle() {
        ByteArray handle = new ByteArray(PasskeyCredentialRegistryAdapter.uuidToBytes(user.id));
        when(userRepository.findByIdOptional(user.id)).thenReturn(Optional.of(user));

        Optional<String> result = adapter.getUsernameForUserHandle(handle);

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get());
    }

    @Test
    void shouldLookupCredential() {
        ByteArray credId = new ByteArray(credential.credentialId);
        ByteArray userHandle = new ByteArray(PasskeyCredentialRegistryAdapter.uuidToBytes(user.id));
        when(passkeyCredentialRepository.findByCredentialId(credential.credentialId)).thenReturn(Optional.of(credential));

        Optional<RegisteredCredential> result = adapter.lookup(credId, userHandle);

        assertTrue(result.isPresent());
        assertEquals(credId, result.get().getCredentialId());
    }

    @Test
    void shouldLookupAllCredentials() {
        ByteArray credId = new ByteArray(credential.credentialId);
        when(passkeyCredentialRepository.findByCredentialId(credential.credentialId)).thenReturn(Optional.of(credential));

        Set<RegisteredCredential> result = adapter.lookupAll(credId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldConvertUuidToAndFromBytes() {
        UUID original = UUID.randomUUID();
        byte[] bytes = PasskeyCredentialRegistryAdapter.uuidToBytes(original);
        UUID converted = PasskeyCredentialRegistryAdapter.bytesToUuid(bytes);

        assertEquals(original, converted);
    }
}
