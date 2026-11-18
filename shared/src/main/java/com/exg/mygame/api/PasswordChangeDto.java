package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// A new password, and the current one to prove the caller is the owner and
/// not somebody holding their unlocked phone.
@Mapped
public record PasswordChangeDto(String current, String replacement) {
}
