package com.exg.mygame.api;

import com.codename1.annotations.Mapped;

import java.util.List;

/// Ten one-use codes that stand in for the authenticator app when it is lost.
/// The server keeps only their hashes: this is the one time they are shown.
@Mapped
public record RecoveryCodesDto(List<String> codes) {
}
