package com.exg.mygame.api;

import com.codename1.annotations.rest.GET;
import com.codename1.annotations.rest.Path;
import com.codename1.annotations.rest.Query;
import com.codename1.annotations.rest.RestClient;
import com.codename1.io.rest.Response;
import com.codename1.util.OnComplete;

import java.util.List;

/// The numbers behind a customer's charts, all of their own accounts, and the
/// market candles anyone signed in may see.
@RestClient
public interface InsightsApi {
    /// The account's closing balance for each of the last `days` UTC days
    /// (default 30, at most 366), today last.
    @GET("/api/accounts/{id}/balance-history")
    void balanceHistory(@Path("id") String id, @Query("days") String days,
            OnComplete<Response<List<BalancePointDto>>> callback);

    /// Money in and out of the customer's accounts in `asset` (default USD)
    /// per UTC day over the last `days` (default 30, at most 366).
    @GET("/api/insights/cashflow")
    void cashflow(@Query("asset") String asset, @Query("days") String days,
            OnComplete<Response<List<CashflowDayDto>>> callback);

    /// What the customer holds, per asset, valued in US cents.
    @GET("/api/insights/allocation")
    void allocation(OnComplete<Response<List<AllocationDto>>> callback);

    /// OHLC candles of `interval` milliseconds (default an hour; one minute
    /// to one day), the newest `limit` (default 168, at most 500).
    @GET("/api/exchange/markets/{id}/candles")
    void candles(@Path("id") String id, @Query("interval") String interval,
            @Query("limit") String limit, OnComplete<Response<List<CandleDto>>> callback);
}
