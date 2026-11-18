package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

import java.util.List;

/// The audit hash chain, walked: `firstBrokenSeq` is the first link that
/// does not hold, or 0 when the chain is intact.
@Mapped
public record ChainReportDto(
        boolean intact,
        long checked,
        long headSeq,
        String headHash,
        long firstBrokenSeq,
        List<String> problems) {
}
