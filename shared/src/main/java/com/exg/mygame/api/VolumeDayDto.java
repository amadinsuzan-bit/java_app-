package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Ledger activity on the UTC day starting at `day` in one asset: the
/// entries that moved it and their total debits. An entry that moves two
/// assets -- a trade -- counts in both rows, so `dayEntries` is the number of
/// distinct entries that day across every asset, the same on each of the
/// day's rows; a count per day reads it once rather than summing `entries`.
@Mapped
public record VolumeDayDto(
        long day,
        String asset,
        int entries,
        int dayEntries,
        long volume) {
}
