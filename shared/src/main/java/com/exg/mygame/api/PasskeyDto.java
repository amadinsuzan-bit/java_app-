package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// A passkey on the account. `id` is the credential id, base64url; `created`
/// and `lastUsed` are epoch milliseconds.
@Mapped
public record PasskeyDto(String id, String label, long created, long lastUsed) {
}
