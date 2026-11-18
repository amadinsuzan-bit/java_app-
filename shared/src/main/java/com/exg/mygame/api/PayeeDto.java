package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Someone the caller may pay. Until `coolingOffUntil` only a small amount may
/// reach them, so a payee slipped in by whoever stole a session cannot be
/// emptied into at once.
@Mapped
public record PayeeDto(String id, String email, String nickname, long createdAt, long coolingOffUntil) {
}
