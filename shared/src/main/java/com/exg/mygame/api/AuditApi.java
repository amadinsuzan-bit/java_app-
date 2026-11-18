package com.exg.mygame.api;

import com.codename1.annotations.rest.GET;
import com.codename1.annotations.rest.Path;
import com.codename1.annotations.rest.Query;
import com.codename1.annotations.rest.RestClient;
import com.codename1.io.rest.Response;
import com.codename1.util.OnComplete;

import java.util.List;

/// The audit trail, for staff only -- an ADMIN or an AUDITOR. Reading it is
/// recorded in it (`STAFF_READ`), so the auditors' own reads are on the
/// record the next auditor reads.
@RestClient
public interface AuditApi {
    /// Walks the hash chain from its first link and names the first one that
    /// does not hold.
    @GET("/api/audit/chain")
    void chain(OnComplete<Response<ChainReportDto>> callback);

    /// Links after sequence `after` (default 0), oldest first, at most
    /// `limit` (default 100, at most 500).
    @GET("/api/audit/events")
    void events(@Query("after") String after, @Query("limit") String limit,
            OnComplete<Response<List<AuditEventDto>>> callback);

    /// Everything recorded about one entity -- an account, an entry, a
    /// username, an approval -- as subject or as actor, newest first.
    @GET("/api/audit/trail")
    void trail(@Query("subject") String subject, OnComplete<Response<List<AuditEventDto>>> callback);

    /// Staff reads of the books and the trail, newest first.
    @GET("/api/audit/access-log")
    void accessLog(@Query("limit") String limit, OnComplete<Response<List<AuditEventDto>>> callback);

    /// Exports `name` -- `journal`, `trial-balance` or `audit-chain` -- as
    /// `format` csv (default) or json. The digest of what was handed out is
    /// recorded in the chain.
    @GET("/api/audit/evidence/{name}")
    void evidence(@Path("name") String name, @Query("format") String format,
            @Query("asOf") String asOf, OnComplete<Response<EvidenceDto>> callback);
}
