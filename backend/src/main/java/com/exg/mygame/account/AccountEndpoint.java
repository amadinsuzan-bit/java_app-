package com.exg.mygame.account;

import com.codename1.backend.annotations.Component;
import com.exg.mygame.Caller;
import com.exg.mygame.api.AccountApiServer;
import com.exg.mygame.api.PasskeyDto;
import com.exg.mygame.api.PasskeyRemoveDto;
import com.exg.mygame.money.Devices;
import com.exg.mygame.money.RequestSigning;
import com.exg.mygame.api.PasswordChangeDto;
import com.exg.mygame.api.RecoveryCodesDto;
import com.exg.mygame.api.RegisterDto;
import com.exg.mygame.api.TotpCodeDto;
import com.exg.mygame.api.TotpEnrollmentDto;
import com.exg.mygame.api.TotpStartDto;
import com.exg.mygame.api.UserDto;

import java.util.List;

/// [com.exg.mygame.api.AccountApi], served. Every call but
/// `register` acts on the caller's own account, named by their token and
/// never by the request.
@Component
public class AccountEndpoint implements AccountApiServer {
    private final Accounts accounts;
    private final SecondFactor secondFactor;
    private final PasskeyList passkeys;
    private final RequestSigning signing;
    private final Devices devices;

    public AccountEndpoint(Accounts accounts, SecondFactor secondFactor, PasskeyList passkeys,
            RequestSigning signing, Devices devices) {
        this.signing = signing;
        this.devices = devices;
        this.accounts = accounts;
        this.secondFactor = secondFactor;
        this.passkeys = passkeys;
    }

    @Override
    public UserDto register(RegisterDto request) throws Exception {
        return accounts.register(request);
    }

    @Override
    public UserDto me() throws Exception {
        return accounts.describe(Caller.name());
    }

    @Override
    public UserDto changePassword(PasswordChangeDto change) throws Exception {
        return accounts.changePassword(Caller.name(), change == null ? null : change.current(),
                change == null ? null : change.replacement());
    }

    @Override
    public TotpEnrollmentDto startTotp(TotpStartDto start) throws Exception {
        return secondFactor.start(Caller.name(), start == null ? null : start.password());
    }

    @Override
    public RecoveryCodesDto confirmTotp(TotpCodeDto code) throws Exception {
        return secondFactor.confirm(Caller.name(), code == null ? null : code.code(),
                code == null ? null : code.password());
    }

    @Override
    public RecoveryCodesDto newRecoveryCodes(TotpCodeDto code) throws Exception {
        return secondFactor.renew(Caller.name(), code == null ? null : code.code());
    }

    @Override
    public List<PasskeyDto> passkeys() throws Exception {
        return passkeys.of(Caller.name());
    }

    @Override
    public List<PasskeyDto> removePasskey(String id, String signature, PasskeyRemoveDto request)
            throws Exception {
        String owner = Caller.name();
        // The same step-up as revoking a device: a stolen token must not take
        // away the passkeys the owner signs in with. A device of the account
        // signs it or, with none at hand, the password is entered again.
        if (RequestSigning.present()) {
            signing.require(owner);
        } else {
            devices.recheck(owner, request == null ? null : request.password());
        }
        return passkeys.remove(owner, id);
    }
}
