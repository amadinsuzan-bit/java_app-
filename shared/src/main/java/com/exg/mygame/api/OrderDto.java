package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// One of the caller's orders.
@Mapped
public record OrderDto(String id, String market, String side, String type, long price, long quantity, long budget, long filled, long settled, long fee, String status, long createdAt) {
}
