package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Moves `amount` out of `fromAccountId`: into `toAccountId` when it is the
/// caller's own account, or to the checking account of `payeeId`'s customer.
/// Exactly one of the two is set.
@Mapped
public record TransferDto(String fromAccountId, String toAccountId, String payeeId, long amount, String memo) {
}
