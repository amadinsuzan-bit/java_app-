package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

import java.util.List;

/// One account as a T: its debits on the left, credits on the right, between
/// the balance brought forward at `from` and the balance carried at `to`
/// (both on the normal side). The sides list at most the latest 2000
/// postings and `truncated` says when there were more; the totals and the
/// balances cover every posting in the window.
@Mapped
public record TAccountDto(
        String accountId,
        String code,
        String name,
        String accountClass,
        String normalBalance,
        String asset,
        long from,
        long to,
        long openingBalance,
        List<LedgerLineDto> debits,
        List<LedgerLineDto> credits,
        long totalDebits,
        long totalCredits,
        long closingBalance,
        boolean truncated) {
}
