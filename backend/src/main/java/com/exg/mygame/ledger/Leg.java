package com.exg.mygame.ledger;

/// One side of a movement: `amount` ledger units into (positive) or out of
/// (negative) an account.
public final class Leg {
    private final String accountId;
    private final long amount;

    public Leg(String accountId, long amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public String accountId() {
        return accountId;
    }

    public long amount() {
        return amount;
    }
}
