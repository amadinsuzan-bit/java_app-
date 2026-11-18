package com.exg.mygame;

import com.codename1.system.Lifecycle;
import com.codename1.ui.Form;
import com.exg.mygame.money.Amounts;
import com.exg.mygame.net.Guard;
import com.exg.mygame.net.Session;
import com.exg.mygame.security.DeviceKey;
import com.exg.mygame.security.Keys;
import com.exg.mygame.security.Lock;
import com.exg.mygame.security.Protect;
import com.exg.mygame.ui.Lang;
import com.exg.mygame.ui.LockForm;
import com.exg.mygame.ui.Nav;
import com.exg.mygame.ui.SignInForm;
import com.exg.mygame.ui.Ui;

/// The app. Before anything touches the network the guard goes in (no
/// cleartext, a host allow-list, certificate pins, AppShield); then the
/// stored session is picked up behind the lock.
///
/// Where things are:
///
/// - `net` -- the guard, the session (password, second factor, passkey) and
///   the typed clients generated from the `shared` contracts.
/// - `security` -- the device signing key, request signing, the app lock,
///   screen and clipboard protection, and the Security Center's status.
/// - `ui` -- the screens.
/// - `AppConfig` -- the server, the allowed hosts, the pins and the timeouts.
public class MyGame extends Lifecycle {
    @Override
    public void runApp() {
        Lang.apply();
        Amounts.useLocale();
        if (!Guard.install()) {
            // Fail closed: without the guard nothing would stop cleartext, an
            // unlisted host or an unpinned certificate. No request is made.
            Form fatal = Ui.form("MyGame");
            fatal.add(Ui.title("MyGame cannot start"));
            fatal.add(Ui.text(Lang.tr("The network protection could not be installed, so "
                    + "MyGame will not connect. Reinstall the app.")));
            Ui.show(fatal, false);
            return;
        }
        Protect.install();
        Session.start(AppConfig.serverUrl());
        // A session that ran out while locked is left to the unlock: it reads
        // the stored tokens, finds none that work and asks for a sign-in then.
        // Showing the sign-in form here would replace the lock screen.
        Session.onSignedOut(() -> {
            if (!Lock.isLocked()) {
                signInScreen();
            }
        });
        Nav.install(MyGame::signedOut);
        Lock.install(new Lock.Screens() {
            @Override
            public void showLocked() {
                // With a biometric or PIN set up, signing in again gives up
                // the keys (LockForm says so). Without one, the password IS
                // the unlock, and the same account keeps its device key
                // (Keys#claim still starts over for a different account).
                LockForm.show(() -> Session.signOut(Keys.protectedByUser()
                        ? MyGame::signedOut : MyGame::signInAgain));
            }

            @Override
            public void showUnlocked(final Form previous) {
                // Locking dropped the session from memory (Session#suspend);
                // its tokens are read again now that the key is open.
                Session.restore(() -> {
                    DeviceKey.revokeOrphan();
                    if (previous != null) {
                        Ui.reshow(previous);
                    } else {
                        home();
                    }
                }, MyGame::signInScreen, MyGame::signInScreen);
            }
        });
        if (Session.hasStoredSession() && Keys.exists() && Keys.protectedByUser()) {
            // The stored session is sealed under the app key: the biometric
            // or PIN that opens the key comes first (LockForm), then the
            // session is read.
            Lock.lockAtStart();
        } else if (Session.hasStoredSession() && Keys.openPlain()) {
            // No biometric or PIN set up: the session opens behind the lock,
            // whose only way out is the password (LockForm).
            Session.restore(Lock::lock, MyGame::signInScreen, MyGame::signInScreen);
        } else {
            signInScreen();
        }
    }

    @Override
    public void start() {
        super.start();
        Lock.resumed();
        Protect.resumed();
    }

    @Override
    public void stop() {
        super.stop();
        Lock.paused();
    }

    /// Signed out by the user (the menu, or "Sign in again" on the lock):
    /// every key on this device goes with the session -- the app key, its
    /// biometric and PIN copies and the payment-signing key -- so whoever
    /// signs in next starts from nothing (`Keys.wipe`).
    static void signedOut() {
        Keys.wipe().ready(done -> {
            Lock.reset();
            signInScreen();
        });
    }

    static void signInAgain() {
        Lock.reset();
        signInScreen();
    }

    static void signInScreen() {
        // A key the user did not open is given up here, before the sign-in
        // stores new tokens under whatever key is current (Keys#signedIn).
        Keys.signedIn();
        SignInForm.show(() -> {
            // A fresh sign-in is the strongest unlock there is; whatever was
            // on screen before belonged to the session that ended.
            Lock.reset();
            if (Keys.claim(Session.username())) {
                // The key was another account's: the tokens just stored were
                // sealed under it and are sealed again under the new one.
                Session.resealTokens();
            }
            DeviceKey.revokeOrphan();
            home();
        });
    }

    static void home() {
        Nav.home();
    }
}
