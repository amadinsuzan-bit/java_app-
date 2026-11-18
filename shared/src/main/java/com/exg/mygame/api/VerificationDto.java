package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

import java.util.List;

/// The result of checking the books or the audit chain: `intact`, or the
/// problems found, each naming what does not hold.
@Mapped
public record VerificationDto(boolean intact, List<String> problems) {
}
