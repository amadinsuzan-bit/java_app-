package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

/// An account as its owner sees it.
///
/// @param totpEnabled whether signing in asks for a code from an
///     authenticator app as well as the password
/// @param recoveryCodesLeft how many unused recovery codes the account has;
///     the codes themselves are shown once, when they are made
@Mapped
public record UserDto(
        String username,
        String displayName,
        boolean customer,
        boolean admin,
        boolean totpEnabled,
        int recoveryCodesLeft,
        long createdAt,
        boolean auditor) {
}
