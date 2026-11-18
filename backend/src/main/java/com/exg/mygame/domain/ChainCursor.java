package com.exg.mygame.domain;

import java.time.Instant;
import com.codename1.annotations.Column;
import com.codename1.annotations.Entity;
import com.codename1.annotations.Id;

/// How far the deposit scanner has read a chain: the next block to read, and
/// the hash of the last one read, so a reorganization below the
/// confirmation depth is noticed rather than silently skipped.
@Entity
public class ChainCursor {
    @Id(autoIncrement = false)
    @Column(nullable = false, type = "VARCHAR(12)")
    private String chain = "";

    @Column(nullable = false)
    private long nextBlock;

    @Column(nullable = false, type = "VARCHAR(66)")
    private String lastHash = "";

    @Column(nullable = false)
    private Instant updatedAt = Instant.EPOCH;

    public ChainCursor() {
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public long getNextBlock() {
        return nextBlock;
    }

    public void setNextBlock(long nextBlock) {
        this.nextBlock = nextBlock;
    }

    public String getLastHash() {
        return lastHash;
    }

    public void setLastHash(String lastHash) {
        this.lastHash = lastHash;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
