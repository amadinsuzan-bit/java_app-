package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// A simulated card payment, standing in for the card network's
/// authorization request.
@Mapped
public record CardSpendDto(long amount, String merchant) {
}
