package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// Starts adding an authenticator app. Enrolling one is a step-up, so the
/// account password goes with it whenever the sign-in was made with the
/// password alone (the token's `amr` holds no `mfa`, `otp` or `hwk`): a stolen
/// token, however fresh, must not put the thief's app on the account.
@Mapped
public record TotpStartDto(String password) {
}
