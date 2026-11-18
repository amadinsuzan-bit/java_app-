package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// One line of a balance sheet or income statement: `section` is ASSETS,
/// LIABILITIES, EQUITY, REVENUE or EXPENSES, `amount` is on the normal side in
/// the asset's ledger units and `usdCents` is that amount valued in US cents
/// at the current index price.
@Mapped
public record StatementLineDto(
        String section,
        String code,
        String name,
        String asset,
        long amount,
        long usdCents) {
}
