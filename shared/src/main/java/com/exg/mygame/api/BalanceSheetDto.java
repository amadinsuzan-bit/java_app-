package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

import java.util.List;

/// Assets = liabilities + equity, per asset, as of `asOf`. Equity includes
/// the current, unclosed period's earnings. `balanced` is that identity
/// holding in every asset.
@Mapped
public record BalanceSheetDto(
        long asOf,
        List<StatementLineDto> lines,
        List<StatementTotalDto> totals,
        long totalAssetsUsdCents,
        boolean balanced) {
}
