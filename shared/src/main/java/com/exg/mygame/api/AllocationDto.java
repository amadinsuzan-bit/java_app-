package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// How much of a customer's holdings is in one asset, valued in US cents;
/// `basisPoints` of the total (10000 = all of it).
@Mapped
public record AllocationDto(
        String asset,
        long amount,
        long usdCents,
        int basisPoints) {
}
