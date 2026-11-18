package com.exg.mygame;

/// The roles an account can hold. Every account opened through the app is a
/// customer; ADMIN and AUDITOR are given only by configuration
/// ([account.DemoAccounts]) and never by any request, so no bug in an endpoint
/// can hand them out.
///
/// The three are disjoint, which is the segregation of duties a bank's
/// examiners ask for: a customer moves their own money, an ADMIN operates the
/// books (adjusts, reverses, closes periods -- above a threshold only with a
/// second admin's approval) and can never rewrite the audit chain, and an
/// AUDITOR reads everything and can change nothing. Every read a member of
/// staff makes of the books or the audit trail is itself audited.
public final class Roles {
    public static final String CUSTOMER = "CUSTOMER";
    public static final String ADMIN = "ADMIN";
    public static final String AUDITOR = "AUDITOR";

    private Roles() {
    }
}
