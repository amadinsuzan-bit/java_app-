package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// A second administrator's approval or rejection; `password` is theirs.
@Mapped
public record DecisionDto(
        String password,
        String reason) {
}
