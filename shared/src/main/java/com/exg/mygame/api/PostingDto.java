package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// One line of an account's history: `amount` in (positive) or out (negative).
/// `kind` and `memo` are the ledger entry's -- what moved the money (CARD,
/// TRANSFER, DEPOSIT...) and its description, a merchant or the note the
/// sender typed -- so a statement can say what each line was.
@Mapped
public record PostingDto(String id, String entryId, String asset, long amount, long createdAt,
        String kind, String memo) {
}
