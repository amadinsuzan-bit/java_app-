package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Opens a SAVINGS or CHECKING account in a fiat asset, or a WALLET in a crypto one.
@Mapped
public record OpenAccountDto(String asset, String kind, String name) {
}
