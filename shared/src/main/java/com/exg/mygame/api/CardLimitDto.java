package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// The most a card may spend in 24 hours, in ledger units of its account.
@Mapped
public record CardLimitDto(long dailyLimit) {
}
