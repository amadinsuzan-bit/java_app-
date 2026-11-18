package com.exg.mygame.account;

import com.codename1.backend.Config;
import com.codename1.backend.security.mfa.JdbcTotpRepository;
import com.codename1.security.Base32;
import com.codename1.backend.DataSource;
import com.codename1.backend.annotations.Component;
import com.codename1.backend.annotations.PostConstruct;
import com.codename1.backend.annotations.Value;
import com.exg.mygame.Roles;
import com.exg.mygame.books.Books;
import com.exg.mygame.chain.Chain;
import com.exg.mygame.domain.LedgerAccount;
import com.exg.mygame.exchange.Markets;
import com.exg.mygame.ledger.AccountKinds;
import com.exg.mygame.ledger.Leg;
import com.exg.mygame.ledger.Ledger;

import java.io.IOException;
import java.util.Arrays;

/// The accounts a server starts with.
///
/// Outside a development profile: one administrator, from
/// `mygame.admin.email` and `mygame.admin.password`, and only when both are
/// set -- in the environment, never in a file that is committed. No request
/// can make an administrator, so this is the only way one comes to exist.
///
/// On a development profile (`dev`, and `test` for the tests): demo customers
/// with weeks of history (see [DemoHistory], `mygame.demo.history-days`),
/// two admins and an auditor, all with [#PASSWORD]. Those passwords are in this file for anyone to read, which is
/// why they exist nowhere else.
@Component
public class DemoAccounts {
    public static final String CUSTOMER = "customer@mygame.example";
    public static final String SECOND_CUSTOMER = "customer2@mygame.example";
    public static final String ADMIN = "admin@mygame.example";
    /// A second administrator, so maker-checker can be shown: one makes an
    /// adjustment above the threshold, the other approves it.
    public static final String SECOND_ADMIN = "admin2@mygame.example";
    /// Reads the books and the audit trail, and can change nothing.
    public static final String AUDITOR = "auditor@mygame.example";
    public static final String PASSWORD = "mygame-demo-password";
    /// The demo staff's authenticator-app secret, base32 as an app takes it.
    /// Staff need a second factor to use the console (see SecurityConfig), so
    /// the demo staff have one, and it is public for the same reason the
    /// password is: these accounts exist only on a development profile.
    public static final String STAFF_TOTP_SECRET = "MYGAMEDEMOSTAFFAUTHENTICATOR234";

    private final Accounts accounts;
    private final Ledger ledger;
    private final Config config;
    private final String adminEmail;
    private final String adminPassword;
    private final Markets markets;
    private final DataSource db;
    private final Books books;
    private final int historyDays;

    /// Asked which coins are real: the demo never mints those.
    private final Chain chain;

    public DemoAccounts(Accounts accounts, Ledger ledger, Config config, Markets markets,
            DataSource db, Books books, Chain chain,
            @Value("${mygame.admin.email:}") String adminEmail,
            @Value("${mygame.admin.password:}") String adminPassword,
            @Value("${mygame.demo.history-days:42}") String historyDays) {
        this.markets = markets;
        this.db = db;
        this.books = books;
        this.chain = chain;
        this.historyDays = Math.max(0, Integer.parseInt(historyDays.trim()));
        this.accounts = accounts;
        this.ledger = ledger;
        this.config = config;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @PostConstruct
    public void seed() throws IOException {
        if (adminEmail.length() > 0 && adminPassword.length() > 0) {
            String username = Accounts.email(adminEmail);
            // The same policy as anyone's: a weak admin password fails the
            // start-up rather than guarding the audit log.
            Passwords.check(username, adminPassword);
            staff(username, adminPassword, Roles.ADMIN, "Administrator");
        }
        if (!config.isDevelopmentProfile()) {
            return;
        }
        staff(ADMIN, PASSWORD, Roles.ADMIN, "Administrator");
        staff(SECOND_ADMIN, PASSWORD, Roles.ADMIN, "Second Administrator");
        staff(AUDITOR, PASSWORD, Roles.AUDITOR, "Internal Auditor");
        JdbcTotpRepository apps = JdbcTotpRepository.fromConfig(db, config);
        for (String who : new String[] {ADMIN, SECOND_ADMIN, AUDITOR}) {
            if (apps.find(who) == null) {
                apps.save(who, Base32.decode(STAFF_TOTP_SECRET));
                apps.confirm(who);
            }
        }
        boolean fresh = !accounts.exists(CUSTOMER);
        long opened = DemoHistory.start(historyDays) - 3600000L;
        customer(CUSTOMER, "Riley Customer", 250000, opened);
        customer(SECOND_CUSTOMER, "Robin Customer", 120000, opened);
        if (fresh && historyDays > 0) {
            new DemoHistory(ledger, markets, db, chain).seed(historyDays, CUSTOMER,
                    SECOND_CUSTOMER);
            if (historyDays > 35 && ledger.closedThrough() == 0) {
                // The first four weeks as a closed period, so the demo shows
                // closing entries and retained earnings.
                books.close(AccountKinds.BANK, DemoHistory.start(historyDays) + 28 * Books.DAY
                        - 9 * 3600000L - 1);
            }
        }
    }

    private void staff(String username, String password, String role, String name)
            throws IOException {
        if (accounts.exists(username)) {
            return;
        }
        try {
            accounts.create(username, password, name, role, System.currentTimeMillis());
        } catch (IOException | RuntimeException failed) {
            // Two servers started together against a new database both find
            // no admin and both make one; the second insert fails on the name.
            // create() is one transaction, so the loser left nothing behind.
            if (!accounts.exists(username)) {
                throw failed;
            }
        }
    }

    private void customer(String username, String name, long cents, long at) throws IOException {
        if (accounts.exists(username)) {
            return;
        }
        accounts.create(username, PASSWORD, name, Roles.CUSTOMER, at);
        LedgerAccount checking = ledger.open(username, Accounts.FIRST_ASSET,
                AccountKinds.CHECKING, "Checking");
        // Money comes from the bank's cash account, as a deposit at a branch
        // would: the books balance from the first entry. The key makes a
        // second seeding of the same account a no-op.
        LedgerAccount cash = ledger.system(Accounts.FIRST_ASSET, AccountKinds.SYS_CASH);
        // Dated at the start of the demo history, which follows it.
        ledger.postAt("seed-" + username, "DEPOSIT", AccountKinds.BANK, "Opening deposit",
                Arrays.asList(new Leg(cash.getId(), -cents), new Leg(checking.getId(), cents)),
                at);
    }
}
