package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// One price of an order book: the open quantity at it, and how many orders.
@Mapped
public record LevelDto(long price, long quantity, int orders) {
}
