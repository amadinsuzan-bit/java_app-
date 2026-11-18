package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// An account's closing balance on the UTC day starting at `day`.
@Mapped
public record BalancePointDto(
        long day,
        long balance) {
}
