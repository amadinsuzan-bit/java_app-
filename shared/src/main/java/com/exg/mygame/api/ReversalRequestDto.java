package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Asks to reverse an entry; `password` is the administrator's.
@Mapped
public record ReversalRequestDto(
        String memo,
        String password) {
}
