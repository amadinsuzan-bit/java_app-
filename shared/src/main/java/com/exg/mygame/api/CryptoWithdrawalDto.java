package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Coins out of a wallet to an allow-listed address.
@Mapped
public record CryptoWithdrawalDto(String addressId, long amount) {
}
