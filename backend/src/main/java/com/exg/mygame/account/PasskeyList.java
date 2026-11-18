package com.exg.mygame.account;

import com.exg.mygame.Refused;
import com.codename1.backend.Base64Url;
import com.codename1.backend.annotations.Component;
import com.codename1.backend.security.webauthn.CredentialRecord;
import com.codename1.backend.security.webauthn.PublicKeyCredentialUserEntity;
import com.codename1.backend.security.webauthn.PublicKeyCredentialUserEntityRepository;
import com.codename1.backend.security.webauthn.UserCredentialRepository;
import com.exg.mygame.api.PasskeyDto;
import com.exg.mygame.audit.Audit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/// The passkeys on an account, for the app's Settings screen.
///
/// Adding one stays a WebAuthn ceremony (`/webauthn/register`, on the
/// signed-in session). Listing needs only the caller's token. Removing one
/// needs more: [AccountEndpoint] asks for a signature from one of the
/// account's devices or, with none at hand, the password again -- the same
/// step-up as revoking a device -- so a stolen token alone cannot take away
/// the passkeys the owner signs in with. Every removal is in the audit log.
@Component
public class PasskeyList {
    private final UserCredentialRepository credentials;
    private final PublicKeyCredentialUserEntityRepository holders;
    private final Audit audit;

    public PasskeyList(UserCredentialRepository credentials,
            PublicKeyCredentialUserEntityRepository holders, Audit audit) {
        this.credentials = credentials;
        this.holders = holders;
        this.audit = audit;
    }

    /// `username`'s passkeys, oldest first.
    public List<PasskeyDto> of(String username) {
        List<PasskeyDto> out = new ArrayList<PasskeyDto>();
        PublicKeyCredentialUserEntity holder = holders.findByUsername(username);
        if (holder == null) {
            return out;
        }
        for (CredentialRecord r : credentials.findByUserId(holder.getId())) {
            out.add(new PasskeyDto(Base64Url.encode(r.getCredentialId()), r.getLabel(),
                    r.getCreated(), r.getLastUsed()));
        }
        return out;
    }

    /// Removes `username`'s passkey `id`; 404 for one that is not theirs, so
    /// another account's ids cannot be probed.
    public List<PasskeyDto> remove(String username, String id) throws IOException {
        byte[] credentialId = decode(id);
        PublicKeyCredentialUserEntity holder = holders.findByUsername(username);
        CredentialRecord record = credentialId == null || holder == null
                ? null : credentials.findByCredentialId(credentialId);
        if (record == null || !Arrays.equals(record.getUserEntityUserId(), holder.getId())) {
            throw Refused.with(404, "no-such-passkey", "No such passkey");
        }
        credentials.delete(credentialId);
        audit.append(username, "PASSKEY_REMOVED", username, record.getLabel());
        return of(username);
    }

    private static byte[] decode(String id) {
        try {
            return id == null ? null : Base64Url.decode(id);
        } catch (RuntimeException notBase64) {
            return null;
        }
    }
}
