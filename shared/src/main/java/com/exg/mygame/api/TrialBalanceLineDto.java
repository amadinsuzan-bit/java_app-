package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// One general-ledger account in one asset on a trial balance: its net
/// balance in the debit or the credit column.
@Mapped
public record TrialBalanceLineDto(
        String code,
        String name,
        String accountClass,
        String asset,
        long debit,
        long credit) {
}
