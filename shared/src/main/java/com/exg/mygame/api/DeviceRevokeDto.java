package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Revokes a device. Taking a device off an account is a step-up: the request
/// is signed by one of the account's devices -- another one, or the one being
/// revoked -- or, with no device to sign it, carries the account password. A
/// stolen token alone must not lock the owner's own phone out.
@Mapped
public record DeviceRevokeDto(String password) {
}
