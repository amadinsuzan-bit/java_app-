package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// A six-digit code from the user's authenticator app. Confirming a new app
/// also carries the account password when the sign-in was made with the
/// password alone (see [TotpStartDto]); otherwise it is null.
@Mapped
public record TotpCodeDto(String code, String password) {
}
