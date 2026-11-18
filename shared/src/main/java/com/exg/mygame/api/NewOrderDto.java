package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// An order to place. `side` is BUY or SELL, `type` LIMIT or MARKET. A limit
/// order names `price` and `quantity`; a market sell names `quantity`, and a
/// market buy `budget`: the quote amount to spend, fee included.
@Mapped
public record NewOrderDto(String market, String side, String type, long price, long quantity, long budget) {
}
