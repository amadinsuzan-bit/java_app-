package com.exg.mygame.account;

import com.exg.mygame.Refused;
import com.codename1.backend.annotations.Component;
import com.codename1.backend.security.mfa.RecoveryCodeService;
import com.codename1.backend.security.mfa.TotpEnrollment;
import com.codename1.backend.security.mfa.TotpService;
import com.codename1.backend.annotations.Value;
import com.codename1.backend.security.core.userdetails.JdbcUserDetailsManager;
import com.codename1.backend.security.crypto.PasswordEncoder;
import com.exg.mygame.Caller;
import com.exg.mygame.api.RecoveryCodesDto;
import com.exg.mygame.api.TotpEnrollmentDto;
import com.exg.mygame.audit.Audit;

import java.io.IOException;

/// Adding an authenticator app to an account, and the recovery codes that
/// stand in for it.
///
/// Once an account has a confirmed app, `http.mfa(...)` in
/// [com.exg.mygame.SecurityConfig] holds every password
/// sign-in back until a code from it is posted to `/login/mfa`. The codes are
/// checked, used up and counted by [TotpService]; nothing here sees one at
/// sign-in.
@Component
public class SecondFactor {
    private final TotpService totp;
    private final RecoveryCodeService recoveryCodes;
    private final Audit audit;

    private final JdbcUserDetailsManager users;
    private final PasswordEncoder encoder;
    private final SignInGuard guard;
    private final long freshSeconds;

    public SecondFactor(TotpService totp, RecoveryCodeService recoveryCodes, Audit audit,
            JdbcUserDetailsManager users, PasswordEncoder encoder, SignInGuard guard,
            @Value("${mygame.device.fresh-minutes:10}") String freshMinutes) {
        this.users = users;
        this.encoder = encoder;
        this.guard = guard;
        this.totp = totp;
        this.recoveryCodes = recoveryCodes;
        this.audit = audit;
        this.freshSeconds = Long.parseLong(freshMinutes.trim()) * 60L;
    }

    /// Enrolling an app is a step-up: once it is on, every sign-in needs its
    /// codes, so a stolen token must not be able to put the attacker's app on
    /// the account and lock the customer out. Every caller's sign-in must be
    /// fresh (as for a device), however it was made: a strong sign-in hours
    /// old is a session somebody may have walked away from. A sign-in made
    /// with a second factor or a user-verified passkey (`amr` holding `mfa`,
    /// `otp`, or `hwk` together with `user`) then skips only the password. A password-only one must also
    /// carry the password: freshness alone would let a token stolen in its
    /// first minutes enrol, which is exactly the window a phishing proxy
    /// works in. A wrong password counts toward the lockout.
    private void requireStepUp(String username, String password) throws IOException {
        long age = System.currentTimeMillis() / 1000L - Caller.authTime();
        if (Caller.authTime() < 0 || age > freshSeconds || age < -60L) {
            audit.append(username, "TOTP_REFUSED", username, "sign-in not fresh");
            throw Refused.with(403, "sign-in-first", "Sign in first");
        }
        java.util.List<String> how = Caller.authMethods();
        // As in Devices: a passkey counts only when the user was verified.
        if (how.contains("mfa") || how.contains("otp")
                || (how.contains("hwk") && how.contains("user"))) {
            return;
        }
        guard.recheck(username, password, encoder, "TOTP_REFUSED",
                "enter-password-add-app", "Enter your password to add an authenticator app");
    }

    /// Makes a secret for the caller's app. Refused when they already have
    /// one: starting over replaces the secret and turns the second factor off
    /// until it is confirmed again, which would let somebody holding only an
    /// access token strip the account of its second factor.
    public TotpEnrollmentDto start(String username, String password) throws IOException {
        if (totp.isEnabled(username)) {
            throw Refused.with(409, "account-already-has-authenticator-app", "This account already has an authenticator app");
        }
        requireStepUp(username, password);
        TotpEnrollment enrollment = totp.beginEnrollment(username);
        audit.append(username, "TOTP_STARTED", username, "");
        return new TotpEnrollmentDto(enrollment.getSecret(), enrollment.getOtpauthUri());
    }

    /// Confirms the app with a code it shows, and makes the recovery codes.
    public RecoveryCodesDto confirm(String username, String code, String password)
            throws IOException {
        requireStepUp(username, password);
        if (code == null || !totp.confirmEnrollment(username, code)) {
            audit.append(username, "TOTP_CONFIRM_FAILED", username, "");
            throw Refused.with(400, "code-not-app-shows", "That code is not the one your app shows");
        }
        audit.append(username, "TOTP_ENROLLED", username, "");
        return new RecoveryCodesDto(recoveryCodes.generate(username));
    }

    /// New recovery codes in place of the old, for a caller who proves they
    /// still have the app.
    public RecoveryCodesDto renew(String username, String code) throws IOException {
        if (!totp.isEnabled(username)) {
            throw Refused.with(409, "add-authenticator-app", "Add an authenticator app first");
        }
        if (code == null || !totp.verify(username, code)) {
            audit.append(username, "RECOVERY_CODES_REFUSED", username, "");
            throw Refused.with(403, "code-not-app-shows", "That code is not the one your app shows");
        }
        audit.append(username, "RECOVERY_CODES_RENEWED", username, "");
        return new RecoveryCodesDto(recoveryCodes.generate(username));
    }
}
