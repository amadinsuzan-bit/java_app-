package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

import java.util.List;

/// The result of reconciling the books at `at`: `clean` when there are no
/// breaks.
@Mapped
public record ReconciliationDto(
        long at,
        boolean clean,
        List<ControlDto> controls,
        List<BreakDto> breaks) {
}
