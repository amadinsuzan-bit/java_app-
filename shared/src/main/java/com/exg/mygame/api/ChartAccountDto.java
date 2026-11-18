package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// One line of the chart of accounts: a code, its class (ASSET, LIABILITY,
/// EQUITY, REVENUE, EXPENSE), its normal balance (DEBIT or CREDIT) and the
/// ledger account kind it is made of.
@Mapped
public record ChartAccountDto(
        String code,
        String name,
        String accountClass,
        String normalBalance,
        String kind) {
}
