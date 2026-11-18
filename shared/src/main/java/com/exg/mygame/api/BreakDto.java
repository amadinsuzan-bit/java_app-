package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// One reconciliation break. `type` is CACHED_BALANCE (an account's kept
/// balance is not its postings), CONTROL (a control account and its
/// sub-ledger disagree) or UNBALANCED_ENTRY (an entry's debits are not its
/// credits).
@Mapped
public record BreakDto(
        String type,
        String asset,
        String reference,
        long expected,
        long actual,
        long difference) {
}
