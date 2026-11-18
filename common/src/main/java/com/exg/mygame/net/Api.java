package com.exg.mygame.net;

import com.codename1.io.rest.RestClients;
import com.exg.mygame.api.AccountApi;
import com.exg.mygame.api.AdminApi;
import com.exg.mygame.api.AuditApi;
import com.exg.mygame.api.BooksApi;
import com.exg.mygame.api.InsightsApi;
import com.exg.mygame.api.BankingApi;
import com.exg.mygame.api.ExchangeApi;

/// The typed clients, generated at build time from the contracts in the
/// `shared` module. Nothing else in the app builds an API URL.
public final class Api {
    private static AccountApi account;
    private static BankingApi banking;
    private static ExchangeApi exchange;
    private static AdminApi admin;
    private static BooksApi books;
    private static AuditApi audit;
    private static InsightsApi insights;

    private Api() {
    }

    static void connect(String baseUrl) {
        account = RestClients.create(AccountApi.class, baseUrl);
        banking = RestClients.create(BankingApi.class, baseUrl);
        exchange = RestClients.create(ExchangeApi.class, baseUrl);
        admin = RestClients.create(AdminApi.class, baseUrl);
        books = RestClients.create(BooksApi.class, baseUrl);
        audit = RestClients.create(AuditApi.class, baseUrl);
        insights = RestClients.create(InsightsApi.class, baseUrl);
    }

    public static AccountApi account() {
        return account;
    }

    public static BankingApi banking() {
        return banking;
    }

    public static ExchangeApi exchange() {
        return exchange;
    }

    public static AdminApi admin() {
        return admin;
    }

    /// The bank's own books; staff only (admin or auditor).
    public static BooksApi books() {
        return books;
    }

    /// The audit log, read by staff; staff only.
    public static AuditApi audit() {
        return audit;
    }

    /// Charts of a customer's own money, and market candles.
    public static InsightsApi insights() {
        return insights;
    }
}
