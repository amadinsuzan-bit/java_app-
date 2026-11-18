package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

import java.util.List;

/// A journal entry with all its legs. `reverses` names the entry this one
/// reverses and `reversedBy` the entry that reversed this one; either is
/// empty when there is none.
@Mapped
public record JournalEntryDto(
        String id,
        long at,
        String kind,
        String initiator,
        String memo,
        String reverses,
        String reversedBy,
        List<JournalLegDto> legs) {
}
