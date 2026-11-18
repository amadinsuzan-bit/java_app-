package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

import java.util.List;

/// Revenue less expenses between `from` and `to`, per asset, closing entries
/// excluded so a closed period still shows what it earned.
@Mapped
public record IncomeStatementDto(
        long from,
        long to,
        List<StatementLineDto> lines,
        List<StatementTotalDto> totals,
        long netIncomeUsdCents) {
}
