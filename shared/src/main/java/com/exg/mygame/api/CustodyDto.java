package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Proof of reserves for one coin: what the chain holds (`onChain`, the hot
/// wallet plus every deposit address, readable on any explorer), what the
/// ledger owes customers (`owed`, in transit included), and the hot wallet's
/// own address and balance.
@Mapped
public record CustodyDto(boolean live, String asset, long chainId, String hotWallet, long hotBalance, long onChain, long owed, long inTransit) {
}
