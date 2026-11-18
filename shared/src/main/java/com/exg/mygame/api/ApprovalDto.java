package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// An administrator's adjustment or reversal. `status` is PENDING (waits for
/// a second administrator), EXECUTED or REJECTED; `kind` ADJUSTMENT or
/// REVERSAL. `resultEntryId` is the entry it posted.
@Mapped
public record ApprovalDto(
        String id,
        String kind,
        String status,
        String maker,
        String checker,
        String accountId,
        String entryId,
        String asset,
        long amount,
        long usdCents,
        String memo,
        String resultEntryId,
        long createdAt,
        long decidedAt) {
}
