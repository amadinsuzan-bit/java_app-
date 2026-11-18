package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Removes a passkey. A step-up like revoking a device: the request is
/// signed by one of the account's devices or, with none to sign it, carries
/// the account password. A stolen token alone must not strip the owner's
/// passkeys, which are how they sign in again.
@Mapped
public record PasskeyRemoveDto(String password) {
}
