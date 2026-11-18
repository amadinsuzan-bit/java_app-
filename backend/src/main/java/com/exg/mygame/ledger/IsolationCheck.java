package com.exg.mygame.ledger;

import com.codename1.backend.DataSource;
import com.codename1.backend.annotations.Component;
import com.codename1.backend.annotations.PostConstruct;
import java.io.IOException;
import java.util.Map;

/// Refuses to start on a MySQL server that runs at REPEATABLE READ, its
/// default.
///
/// The ledger, the audit chain and the order book each serialize their
/// writers on one row -- a balance, `audit_head`, a market -- and then read
/// what the writer before them committed. At REPEATABLE READ, InnoDB answers
/// those reads from the snapshot the transaction took at its first read,
/// which can predate the lock: two appends then both chain onto the same
/// audit row, and a cancel releases funds a concurrent fill already moved.
/// READ COMMITTED reads what was committed when each statement runs, as
/// PostgreSQL does by default; SQLite serializes every writer and needs
/// nothing.
///
/// The level is the server's (or the account's) setting, not the
/// application's, so it is checked rather than set:
/// `SET GLOBAL transaction_isolation = 'READ-COMMITTED'`, or
/// `transaction-isolation = READ-COMMITTED` in the server's configuration.
@Component
public class IsolationCheck {
    private final DataSource db;

    public IsolationCheck(DataSource db) {
        this.db = db;
    }

    @PostConstruct
    public void check() throws IOException {
        if (!"mysql".equals(db.dialect().getName())) {
            return;
        }
        Object level = level(sql -> {
            Map row = db.queryOne(sql, null);
            return row == null ? null : row.get("level");
        });
        if (!"READ-COMMITTED".equals(String.valueOf(level))) {
            throw new IllegalStateException("MyGame needs MySQL at READ COMMITTED, and this "
                    + "server runs at " + level + ": SET GLOBAL transaction_isolation = "
                    + "'READ-COMMITTED' (see IsolationCheck)");
        }
    }

    /// One query against the server.
    public interface Query {
        Object level(String sql) throws IOException;
    }

    /// The server's isolation level. `@@transaction_isolation` is MySQL's
    /// (since 5.7.20) and MariaDB's only from 11.1; older MariaDB -- 10.11
    /// LTS among them -- knows it only as `@@tx_isolation`, and refuses the
    /// first with "Unknown system variable". So the second is asked when the
    /// first is refused.
    public static Object level(Query q) throws IOException {
        try {
            return q.level("SELECT @@transaction_isolation AS level");
        } catch (IOException | RuntimeException unknown) {
            return q.level("SELECT @@tx_isolation AS level");
        }
    }
}
