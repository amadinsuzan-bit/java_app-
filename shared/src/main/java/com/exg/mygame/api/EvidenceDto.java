package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// An exported piece of evidence: `content` in `format` (csv or json), and
/// the SHA-256 of its UTF-8 bytes, which is also recorded in the audit chain
/// when it is exported.
@Mapped
public record EvidenceDto(
        String name,
        String format,
        String contentType,
        long generatedAt,
        String generatedBy,
        String sha256,
        String content) {
}
