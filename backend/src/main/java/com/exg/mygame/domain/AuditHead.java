package com.exg.mygame.domain;

import com.codename1.annotations.Column;
import com.codename1.annotations.Entity;
import com.codename1.annotations.Id;

/// The newest link of the audit chain; one row.
@Entity
public class AuditHead {
    @Id(autoIncrement = false)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private long seq;

    @Column(nullable = false, type = "VARCHAR(64)")
    private String hash = "";

    public AuditHead() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
