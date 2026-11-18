package com.exg.mygame;

import com.codename1.backend.ResponseStatusException;
import com.exg.mygame.api.Refusal;

/// A refusal the app can translate: the status, a stable code and the English
/// sentence, worded by [Refusal].
public final class Refused {
    private Refused() {
    }

    /// `english` names `values` as `{0}`, `{1}`...; the app's bundle has the
    /// same sentence under `error.<code>`.
    public static ResponseStatusException with(int status, String code, String english,
            Object... values) {
        return new ResponseStatusException(status, Refusal.format(code, english, values));
    }
}
