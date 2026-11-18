package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Money in and out on the UTC day starting at `day`; moves between the
/// customer's own accounts are left out.
@Mapped
public record CashflowDayDto(
        long day,
        long inflow,
        long outflow,
        long net) {
}
