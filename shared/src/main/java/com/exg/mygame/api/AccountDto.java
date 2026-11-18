package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// A balance the caller holds, in ledger units of its asset (see [AssetDto]).
@Mapped
public record AccountDto(String id, String asset, String kind, String name, long balance, boolean frozen, long createdAt) {
}
