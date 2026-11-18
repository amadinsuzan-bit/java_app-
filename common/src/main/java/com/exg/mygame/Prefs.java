package com.exg.mygame;

import com.codename1.io.Preferences;

/// The few choices the user makes on this device. Nothing here is a secret:
/// a choice is a preference, and every secret is in secure storage.
public final class Prefs {
    private static final String ONBOARDED = "mygame.onboarded.";

    private Prefs() {
    }

    /// Whether `user` has been through onboarding on this device; the steps
    /// they skipped are offered again until then.
    public static boolean onboarded(String user) {
        return user != null && Preferences.get(ONBOARDED + user, false);
    }

    public static void setOnboarded(String user) {
        if (user != null) {
            Preferences.set(ONBOARDED + user, true);
        }
    }
}
