package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Coins that crossed to or from the chain: `direction` IN or OUT, `status`
/// QUEUED, SIGNED, BROADCAST, CONFIRMED or FAILED, `txHash` the transaction a
/// block explorer shows (empty until signed).
@Mapped
public record ChainTransferDto(String id, String direction, String asset, String address, long amount, String status, String txHash, long blockNumber, long fee, String detail, long createdAt) {
}
