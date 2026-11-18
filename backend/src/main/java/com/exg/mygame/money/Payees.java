package com.exg.mygame.money;

import java.time.Instant;
import com.exg.mygame.repository.ProfileRepository;
import com.exg.mygame.Refused;
import com.codename1.backend.ResponseStatusException;
import com.codename1.backend.annotations.Component;
import com.codename1.backend.annotations.Transactional;
import com.codename1.backend.annotations.Value;
import com.exg.mygame.Ids;
import com.exg.mygame.account.Accounts;
import com.exg.mygame.api.NewPayeeDto;
import com.exg.mygame.api.PayeeDto;
import com.exg.mygame.audit.Audit;
import com.exg.mygame.domain.Payee;
import com.exg.mygame.repository.PayeeRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/// The people a customer may pay: other customers of this bank, by e-mail.
///
/// A payee is where stolen money goes, so adding one takes a signed request
/// from a trusted device, and a new payee is in **cooling-off** for
/// `mygame.payee.cooloff-hours`: in that time it can receive no more than
/// `mygame.payee.cooloff-limit` in all. Whoever takes over a session can add
/// themselves, but cannot empty the account into themselves before the owner
/// sees the notification and the audit trail.
///
/// A removed payee is flagged, never deleted, and adding the same address
/// again restores that row with its id and creation time: the cooling-off is
/// summed by payee id from that time, so a fresh row would have handed out a
/// fresh allowance to anyone who removes and re-adds. For the same reason
/// new payees are capped per day (`mygame.payee.per-day`), removed ones
/// counted: otherwise a thief adds a payee, pays it, removes it and adds
/// another address.
@Component
public class Payees {
    static final int MAX_PAYEES = 200;
    static final long DAY = 24L * 3600000L;

    private final PayeeRepository payees;
    private final ProfileRepository profiles;
    private final Money money;
    private final Audit audit;
    private final int perDay;

    public Payees(PayeeRepository payees, Money money, Audit audit,
            ProfileRepository profiles,
            @Value("${mygame.payee.per-day:5}") String perDay) {
        this.perDay = Integer.parseInt(perDay.trim());
        this.payees = payees;
        this.profiles = profiles;
        this.money = money;
        this.audit = audit;
    }

    @Transactional(rollbackFor = Exception.class)
    public PayeeDto add(String owner, NewPayeeDto request) throws IOException {
        if (request == null) {
            throw Refused.with(400, "name-payee", "Name the payee");
        }
        String username;
        try {
            username = Accounts.email(request.email());
        } catch (ResponseStatusException malformed) {
            throw Refused.with(400, "not-e-mail-address", "That is not an e-mail address");
        }
        if (username.equals(owner)) {
            throw Refused.with(400, "move-money-between-own-accounts", "Move money between your own accounts instead");
        }
        // The same answer whether or not anyone banks under that address:
        // "no such customer" turned this into a directory of who banks here,
        // one signed request per guess. A payee whose address has no account
        // is added like any other and simply cannot be paid -- the payment is
        // refused with "That payee cannot receive", as it is for a payee with
        // no account in that currency, so nothing tells the two apart. (Should
        // somebody register that address later, they become payable from the
        // payee's own creation time; the owner chose the address.)
        // Behind the customer's profile row: two of these at once would each
        // count the rows before the other inserted and together pass the cap.
        profiles.hold(owner);
        Payee known = payees.findFirstByOwnerAndUsername(owner, username);
        if (known != null && !known.isRemoved()) {
            throw Refused.with(409, "payee-already-list", "That payee is already on your list");
        }
        if (known != null) {
            // Back as it was: the same id and the same creation time, so
            // whatever it was paid during its cooling-off still counts.
            known.setRemoved(false);
            known.setNickname(nickname(request.nickname(), username));
            payees.save(known);
            audit.append(owner, "PAYEE_RESTORED", known.getId(), username);
            return dto(known);
        }
        long now = System.currentTimeMillis();
        int live = 0;
        int today = 0;
        for (Payee p : payees.findByOwnerOrderByCreatedAtAsc(owner)) {
            live += p.isRemoved() ? 0 : 1;
            today += p.getCreatedAt().toEpochMilli() > now - DAY ? 1 : 0;
        }
        if (live >= MAX_PAYEES) {
            throw Refused.with(409, "remove-payee-before-adding-another", "Remove a payee before adding another");
        }
        if (today >= perDay) {
            throw Refused.with(429, "too-many-new-payees", "You have added as many payees as a day allows; try again tomorrow");
        }
        Payee payee = new Payee();
        payee.setId(Ids.next());
        payee.setOwner(owner);
        payee.setUsername(username);
        payee.setNickname(nickname(request.nickname(), username));
        payee.setCreatedAt(Instant.ofEpochMilli(now));
        payees.insert(payee);
        audit.append(owner, "PAYEE_ADDED", payee.getId(), username);
        return dto(payee);
    }

    @Transactional(readOnly = true)
    public List<PayeeDto> list(String owner) throws IOException {
        List<PayeeDto> out = new ArrayList<PayeeDto>();
        for (Payee p : payees.findByOwnerOrderByCreatedAtAsc(owner)) {
            if (!p.isRemoved()) {
                out.add(dto(p));
            }
        }
        return out;
    }

    /// `owner`'s payee `id`, or 404 whether it is missing, removed or someone
    /// else's: a removed payee cannot be paid or renamed.
    @Transactional(readOnly = true)
    public Payee owned(String owner, String id) throws IOException {
        Payee payee = id == null ? null : payees.findById(id);
        if (payee == null || payee.isRemoved() || !payee.getOwner().equals(owner)) {
            throw Refused.with(404, "no-such-payee", "No such payee");
        }
        return payee;
    }

    @Transactional(rollbackFor = Exception.class)
    public PayeeDto rename(String owner, String id, NewPayeeDto request) throws IOException {
        Payee payee = owned(owner, id);
        // Only the nickname: pointing an existing payee at somebody else would
        // skip the new payee's cooling-off and signature.
        payee.setNickname(nickname(request == null ? null : request.nickname(),
                payee.getUsername()));
        payees.save(payee);
        audit.append(owner, "PAYEE_RENAMED", id, payee.getNickname());
        return dto(payee);
    }

    @Transactional(rollbackFor = Exception.class)
    public PayeeDto remove(String owner, String id) throws IOException {
        Payee payee = owned(owner, id);
        payee.setRemoved(true);
        payees.save(payee);
        audit.append(owner, "PAYEE_REMOVED", id, payee.getUsername());
        return dto(payee);
    }

    private PayeeDto dto(Payee p) {
        return new PayeeDto(p.getId(), p.getUsername(), p.getNickname(), p.getCreatedAt().toEpochMilli(),
                p.getCreatedAt().toEpochMilli() + money.coolOffMillis());
    }

    private static String nickname(String value, String fallback) {
        String v = value == null ? "" : value.trim();
        if (v.length() == 0) {
            v = fallback;
        }
        return v.length() <= 80 ? v : v.substring(0, 80);
    }
}
