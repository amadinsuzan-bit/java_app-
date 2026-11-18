package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// An allow-listed withdrawal address, usable from `usableAt`.
@Mapped
public record CryptoAddressDto(String id, String asset, String address, String label, long usableAt, long createdAt) {
}
