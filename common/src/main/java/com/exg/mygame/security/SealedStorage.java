package com.exg.mygame.security;

import com.codename1.security.SecureStorage;
import com.codename1.security.vault.AssociatedData;
import com.codename1.security.vault.ProtectionReport;
import com.codename1.security.vault.SecureEnvelope;
import com.codename1.util.Base64;

/// The store the session's tokens are kept in: each value sealed under the
/// app key ([Keys]) as an AES-256-GCM [SecureEnvelope], and the sealed text
/// kept in the OS store's quiet tier.
///
/// So the refresh token is under the same lock as the payment key: with a
/// biometric or PIN set up, a stolen phone that is not unlocked has a
/// ciphertext whose key the OS will not release, not a token that can be
/// replayed. While the app is locked [#get(String)] answers null and
/// [#set(String, String)] refuses; the session reads its tokens after the
/// unlock (`MyGame.runApp`).
///
/// Only the quiet methods are implemented: the token store never asks this
/// store for a biometric -- the biometric is what opens the key.
public final class SealedStorage extends SecureStorage {
    private final SecureStorage inner;

    public SealedStorage(SecureStorage inner) {
        this.inner = inner;
    }

    @Override
    public boolean set(String account, String value) {
        byte[] key = Keys.current();
        if (key == null) {
            return false;
        }
        byte[] sealed = SecureEnvelope.seal(key, "app-key", 1, aad(account),
                bytes(value));
        return inner.set(account, Base64.encodeUrlSafe(sealed));
    }

    @Override
    public String get(String account) {
        byte[] key = Keys.current();
        String stored = inner.get(account);
        if (key == null || stored == null) {
            return null;
        }
        try {
            byte[] opened = SecureEnvelope.parse(Base64.decodeUrlSafe(stored))
                    .open(key, aad(account));
            return text(opened);
        } catch (RuntimeException sealedUnderAnotherKey) {
            // A token sealed under a key since wiped: no session to restore.
            remove(account);
            return null;
        }
    }

    @Override
    public boolean remove(String account) {
        return inner.remove(account);
    }

    @Override
    public int entryState(String account) {
        return inner.entryState(account);
    }

    @Override
    public ProtectionReport protection() {
        return inner.protection();
    }

    private static AssociatedData aad(String account) {
        return AssociatedData.of("mygame", "session", account, "oidc-tokens");
    }

    private static byte[] bytes(String value) {
        try {
            return value.getBytes("UTF-8");
        } catch (java.io.UnsupportedEncodingException never) {
            return value.getBytes();
        }
    }

    private static String text(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (java.io.UnsupportedEncodingException never) {
            return new String(bytes);
        }
    }
}
