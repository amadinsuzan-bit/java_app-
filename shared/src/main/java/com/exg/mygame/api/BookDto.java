package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

import java.util.List;

/// A market's order book, aggregated by price: best first on both sides. It
/// names no order and no owner.
@Mapped
public record BookDto(String market, List<LevelDto> bids, List<LevelDto> asks) {
}
