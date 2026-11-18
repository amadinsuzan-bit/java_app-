package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// The simulated price of a market and the best of its book; 0 where a side
/// is empty or nothing has traded.
@Mapped
public record TickerDto(String market, long indexPrice, long lastPrice, long bestBid, long bestAsk, long at) {
}
