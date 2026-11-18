package com.exg.mygame.domain;

import java.time.Instant;
import com.codename1.annotations.Column;
import com.codename1.annotations.Entity;
import com.codename1.annotations.Id;

/// Wrong passwords counted against one account, for the lockout in
/// [com.exg.mygame.account.SignInGuard]. Kept in the
/// database and not in memory so that every instance of the server counts the
/// same attempts: spreading guesses over instances buys an attacker nothing.
@Entity
public class LoginFailure {
    @Id(autoIncrement = false)
    @Column(nullable = false, type = "VARCHAR(190)")
    private String username = "";

    @Column(nullable = false)
    private int failures;

    @Column(nullable = false)
    private Instant firstAt = Instant.EPOCH;

    @Column(nullable = false)
    private Instant lockedUntil = Instant.EPOCH;

    public LoginFailure() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getFailures() {
        return failures;
    }

    public void setFailures(int failures) {
        this.failures = failures;
    }

    public Instant getFirstAt() {
        return firstAt;
    }

    public void setFirstAt(Instant firstAt) {
        this.firstAt = firstAt;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(Instant lockedUntil) {
        this.lockedUntil = lockedUntil;
    }
}
