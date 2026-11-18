package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

import java.util.List;

/// A closed accounting period: everything at or before `closedThrough` is
/// final; `entries` are the closing entries it posted.
@Mapped
public record PeriodDto(
        long seq,
        long closedThrough,
        String closedBy,
        long closedAt,
        List<String> entries) {
}
