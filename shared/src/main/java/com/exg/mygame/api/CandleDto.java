package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// An OHLC candle starting at `start`; `volume` is base ledger units traded,
/// `quoteVolume` quote units paid. A candle with no trades carries the
/// previous close.
@Mapped
public record CandleDto(
        long start,
        long open,
        long high,
        long low,
        long close,
        long volume,
        long quoteVolume,
        int trades) {
}
