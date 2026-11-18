package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Something an account can hold. `decimals` is how many ledger units make
/// one whole unit: an amount of 12345 in an asset with 2 decimals is 123.45.
@Mapped
public record AssetDto(String code, String name, String kind, int decimals) {
}
