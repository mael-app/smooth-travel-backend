package com.smoothtravel.passkey.entity;

import com.smoothtravel.user.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "passkey_credentials")
public class PasskeyCredential extends PanacheEntityBase {

    @Id
    @GeneratedValue
    @UuidGenerator
    public UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Column(name = "credential_id", nullable = false, unique = true)
    public byte[] credentialId;

    @Column(name = "public_key_cose", nullable = false)
    public byte[] publicKeyCose;

    @Column(name = "signature_count", nullable = false)
    public long signatureCount;

    @Column(name = "display_name")
    public String displayName;

    @Column(name = "transports")
    public String transports;

    @Column(name = "discoverable", nullable = false)
    public boolean discoverable;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;

    @PrePersist
    void onPrePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onPreUpdate() {
        updatedAt = Instant.now();
    }
}
