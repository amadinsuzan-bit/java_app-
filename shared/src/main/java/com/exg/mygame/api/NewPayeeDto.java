package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// A payee by the e-mail address their customer account is under.
@Mapped
public record NewPayeeDto(String email, String nickname) {
}
