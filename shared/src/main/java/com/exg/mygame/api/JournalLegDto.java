package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// One leg of a journal entry, as Dr/Cr: exactly one of `debit` and `credit`
/// is non-zero, in the asset's ledger units.
@Mapped
public record JournalLegDto(
        String accountId,
        String code,
        String accountName,
        String owner,
        String asset,
        long debit,
        long credit) {
}
