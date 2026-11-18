package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

import java.util.List;

/// A trial balance as of `asOf`, per asset.
@Mapped
public record TrialBalanceDto(
        long asOf,
        List<TrialBalanceLineDto> lines,
        List<AssetTotalDto> totals,
        boolean balanced) {
}
