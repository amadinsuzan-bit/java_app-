package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// One link of the audit chain, with both hashes so it can be checked
/// independently.
@Mapped
public record AuditEventDto(
        long seq,
        long at,
        String actor,
        String action,
        String subject,
        String detail,
        String address,
        String prevHash,
        String hash) {
}
