package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// A manual adjustment: `amount` ledger units credited to (positive) or
/// debited from (negative) the customer account, against operational
/// adjustments expense. `password` is the administrator's.
@Mapped
public record AdjustmentDto(
        String accountId,
        long amount,
        String memo,
        String password) {
}
