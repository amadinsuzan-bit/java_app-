package com.exg.mygame.domain;

import com.codename1.annotations.Column;
import com.codename1.annotations.Entity;
import com.codename1.annotations.Id;

/// Something an account can hold: a currency or a coin. The ledger treats
/// both alike; `decimals` says how many ledger units make one whole unit.
@Entity
public class Asset {
    @Id(autoIncrement = false)
    @Column(nullable = false, type = "VARCHAR(12)")
    private String code = "";

    @Column(nullable = false, type = "VARCHAR(60)")
    private String name = "";

    @Column(nullable = false, type = "VARCHAR(8)")
    private String kind = "";

    @Column(nullable = false)
    private int decimals;

    @Column(nullable = false)
    private boolean enabled;

    public Asset() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
