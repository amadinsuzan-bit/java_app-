package com.exg.mygame.books;

/// Test-only access to the package-private seams of this package, so the
/// production class exposes none of them publicly.
public final class ApprovalsTestHooks {
    private ApprovalsTestHooks() {
    }

    /// Runs `pause` between reading a maker's 24h total and acting on it;
    /// null removes it.
    public static void pauseAfterRecentTotal(Runnable pause) {
        Approvals.TEST_ONLY_AFTER_RECENT_TOTAL.set(pause);
    }
}
