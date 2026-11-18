package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Closes the books through `through` (epoch millis, in the past and after
/// the last close). `password` is the administrator's, as step-up.
@Mapped
public record ClosePeriodDto(
        long through,
        String password) {
}
