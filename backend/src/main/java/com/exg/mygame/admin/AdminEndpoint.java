package com.exg.mygame.admin;

import com.exg.mygame.Refused;
import com.codename1.backend.DataSource;
import com.codename1.backend.annotations.Component;
import com.codename1.backend.security.crypto.PasswordEncoder;
import com.exg.mygame.Caller;
import com.exg.mygame.Roles;
import com.exg.mygame.api.AdminApiServer;
import com.exg.mygame.api.AdjustmentDto;
import com.exg.mygame.api.ApprovalDto;
import com.exg.mygame.api.ClosePeriodDto;
import com.exg.mygame.api.DecisionDto;
import com.exg.mygame.api.PeriodDto;
import com.exg.mygame.api.ReversalRequestDto;
import com.exg.mygame.api.VerificationDto;
import com.exg.mygame.account.SignInGuard;
import com.exg.mygame.books.Approvals;
import com.exg.mygame.books.Books;
import com.exg.mygame.audit.Audit;
import com.exg.mygame.ledger.Ledger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/// [com.exg.mygame.api.AdminApi], served.
///
/// `/api/admin/**` is already refused to anyone without ADMIN by the API
/// chain's rules. Each call asks again here: a rule is one line in another
/// file, and a route moved out from under it would otherwise be open to every
/// customer.
@Component
public class AdminEndpoint implements AdminApiServer {
    private final Audit audit;
    private final Ledger ledger;
    private final DataSource db;

    private final Books books;
    private final Approvals approvals;
    private final SignInGuard guard;
    private final PasswordEncoder encoder;

    public AdminEndpoint(Audit audit, Ledger ledger, DataSource db, Books books,
            Approvals approvals, SignInGuard guard, PasswordEncoder encoder) {
        this.books = books;
        this.approvals = approvals;
        this.guard = guard;
        this.encoder = encoder;
        this.audit = audit;
        this.ledger = ledger;
        this.db = db;
    }

    @Override
    public VerificationDto verifyAudit() throws Exception {
        String admin = admin();
        List<String> problems = audit.verify();
        // Who looked, and what they saw, is itself on the record. Appended
        // after the walk so the check is of the chain as it was asked about.
        audit.append(admin, "AUDIT_VERIFIED", "audit", problems.isEmpty() ? "intact"
                : problems.size() + " problems");
        return new VerificationDto(problems.isEmpty(), problems);
    }

    @Override
    public VerificationDto verifyLedger() throws Exception {
        String admin = admin();
        List rows = db.query("SELECT DISTINCT owner FROM ledger_account", new Object[0]);
        List<String> owners = new ArrayList<String>();
        for (int iter = 0; rows != null && iter < rows.size(); iter++) {
            Object row = rows.get(iter);
            Object owner = row instanceof Map ? ((Map) row).get("owner") : null;
            if (owner != null) {
                owners.add(String.valueOf(owner));
            }
        }
        List<String> problems = ledger.verify(owners);
        audit.append(admin, "LEDGER_VERIFIED", "ledger", problems.isEmpty() ? "balanced"
                : problems.size() + " problems");
        return new VerificationDto(problems.isEmpty(), problems);
    }

    @Override
    public PeriodDto closePeriod(ClosePeriodDto request) throws Exception {
        String admin = stepUp(request == null ? null : request.password());
        return books.close(admin, request.through());
    }

    @Override
    public ApprovalDto adjust(String idempotencyKey, AdjustmentDto request) throws Exception {
        String admin = stepUp(request == null ? null : request.password());
        return approvals.adjust(admin, idempotencyKey, request.accountId(), request.amount(),
                request.memo());
    }

    @Override
    public ApprovalDto reverse(String id, String idempotencyKey, ReversalRequestDto request)
            throws Exception {
        String admin = stepUp(request == null ? null : request.password());
        return approvals.reverse(admin, idempotencyKey, id, request.memo());
    }

    @Override
    public List<ApprovalDto> approvals() throws Exception {
        admin();
        return approvals.list();
    }

    @Override
    public ApprovalDto approve(String id, DecisionDto decision) throws Exception {
        String admin = stepUp(decision == null ? null : decision.password());
        return approvals.approve(admin, id);
    }

    @Override
    public ApprovalDto reject(String id, DecisionDto decision) throws Exception {
        String admin = stepUp(decision == null ? null : decision.password());
        return approvals.reject(admin, id, decision.reason());
    }

    /// The administrator, having typed their password again: every change
    /// to the books is a step-up, held to the sign-in lockout like any other
    /// password guess. Checked -- and a refusal audited -- before the
    /// change's own transaction, which would roll the record back with it.
    private String stepUp(String password) throws Exception {
        String admin = admin();
        guard.recheck(admin, password, encoder, "STAFF_STEP_UP_REFUSED",
                "enter-password-confirm", "Enter your password to confirm");
        return admin;
    }

    private static String admin() {
        String name = Caller.name();
        if (!Caller.hasRole(Roles.ADMIN)) {
            throw Refused.with(403, "administrators", "Administrators only");
        }
        return name;
    }
}
