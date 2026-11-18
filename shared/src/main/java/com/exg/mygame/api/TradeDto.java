package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// A fill of one of the caller's orders, from the caller's side.
@Mapped
public record TradeDto(String id, String market, String side, boolean taker, long price, long quantity, long quote, long fee, long createdAt) {
}
