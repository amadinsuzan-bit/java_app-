package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// A market: `base` traded for `quote`. Prices are quote ledger units per
/// one whole base unit; `lot` is the smallest quantity, in base ledger units.
@Mapped
public record MarketDto(String id, String base, String quote, long lot, long indexPrice, long lastPrice) {
}
