package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// What a movement of money did. A retry with the same Idempotency-Key answers
/// the same receipt, never a second movement.
@Mapped
public record ReceiptDto(String entryId, String kind, String fromAccountId, String toAccountId, String asset, long amount, String memo, long createdAt) {
}
