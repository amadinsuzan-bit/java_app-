package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// A device that may sign requests for the caller. `trusted` is attested and
/// not rooted: only a trusted device may send money to someone else.
@Mapped
public record DeviceDto(String id, String platform, boolean rooted, boolean attested, boolean trusted, long createdAt) {
}
