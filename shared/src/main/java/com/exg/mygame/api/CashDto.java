package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Money arriving from, or leaving to, the world outside the bank.
@Mapped
public record CashDto(long amount, String memo) {
}
