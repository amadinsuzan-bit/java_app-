package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// An address to allow-list for withdrawals of `asset`.
@Mapped
public record NewCryptoAddressDto(String asset, String address, String label) {
}
