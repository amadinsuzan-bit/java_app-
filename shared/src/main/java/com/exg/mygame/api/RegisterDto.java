package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// What opening an account takes. The password travels once, in the body of a
/// request over TLS, and the server keeps only its hash.
@Mapped
public record RegisterDto(String email, String password, String displayName) {
}
