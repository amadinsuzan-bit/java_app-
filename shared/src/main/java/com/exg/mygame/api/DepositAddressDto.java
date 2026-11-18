package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Where a customer sends coins to deposit them: their own address on
/// `network` (chain id `chainId`), credited after `confirmations` blocks.
/// `live` is false while the sample's chain is simulated, and the address is
/// then empty.
@Mapped
public record DepositAddressDto(boolean live, String asset, String address, String network, long chainId, int confirmations) {
}
