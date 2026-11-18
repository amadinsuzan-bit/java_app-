package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// What an authenticator app needs: the secret in Base32 for typing in by
/// hand, and the `otpauth://` address a QR code carries. Shown once.
@Mapped
public record TotpEnrollmentDto(String secret, String otpauthUri) {
}
