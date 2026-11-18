package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

import java.util.List;

/// The general ledger of one chart code in one asset between `from` and
/// `to`. `lines` holds at most the latest 2000 postings, oldest first, and
/// `truncated` says when there were more; the totals and both balances are
/// summed over every posting in the window, never just the listed ones.
@Mapped
public record GeneralLedgerDto(
        String code,
        String asset,
        long from,
        long to,
        long openingBalance,
        List<LedgerLineDto> lines,
        long totalDebits,
        long totalCredits,
        long closingBalance,
        boolean truncated) {
}
