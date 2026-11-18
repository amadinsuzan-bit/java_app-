package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// The column totals of a trial balance in one asset; equal, or the books
/// are broken.
@Mapped
public record AssetTotalDto(
        String asset,
        long debits,
        long credits,
        boolean balanced) {
}
