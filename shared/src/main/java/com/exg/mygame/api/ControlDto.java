package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// A control account against its sub-ledger in one asset: `control` is what
/// the general ledger says (postings), `subledger` the sum of the individual
/// accounts' kept balances.
@Mapped
public record ControlDto(
        String asset,
        String code,
        String name,
        long control,
        long subledger,
        long difference) {
}
