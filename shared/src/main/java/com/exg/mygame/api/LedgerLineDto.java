package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// One posting in a general ledger or T-account, with the running `balance`
/// after it, on the account class's normal side.
@Mapped
public record LedgerLineDto(
        String entryId,
        long at,
        String kind,
        String memo,
        String accountId,
        String owner,
        String code,
        String asset,
        long debit,
        long credit,
        long balance) {
}
