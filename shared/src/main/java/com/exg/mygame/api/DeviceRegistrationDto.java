package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Registers a device's signing key, an EC P-256 public key as base64url
/// SubjectPublicKeyInfo DER, with the integrity signals the device reported
/// and the platform integrity token (Play Integrity, App Attest) if it has one.
///
/// `password` is the account password, asked again when the account has no
/// authenticator app: registering a signing key needs a sign-in no more than
/// a few minutes old, and proof beyond the access token that it is the owner.
@Mapped
public record DeviceRegistrationDto(String publicKey, String platform, boolean rooted, boolean attested, String integrityToken,
        String password) {
}
