package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// A statement subtotal in one asset: `section` as on the lines, plus
/// CURRENT_EARNINGS (revenue less expenses not yet closed) on a balance sheet
/// and NET_INCOME on an income statement.
@Mapped
public record StatementTotalDto(
        String asset,
        String section,
        long amount,
        long usdCents) {
}
