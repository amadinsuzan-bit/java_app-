package com.exg.mygame.api;

import com.codename1.annotations.rest.Body;
import com.codename1.annotations.rest.GET;
import com.codename1.annotations.rest.Header;
import com.codename1.annotations.rest.POST;
import com.codename1.annotations.rest.Path;
import com.codename1.annotations.rest.RestClient;
import com.codename1.io.rest.Response;
import com.codename1.util.OnComplete;

import java.util.List;

/// What only an admin may ask or do: whether the records still hold, and
/// the operations on the books -- period close, adjustments and reversals.
///
/// Every operation that changes the books takes the administrator's password
/// (step-up, held to the sign-in lockout) and is audited in its own
/// transaction. An adjustment or reversal worth more than
/// `mygame.approval.threshold-usd-cents` waits PENDING for a different
/// administrator (maker-checker); below it, it posts at once.
@RestClient
public interface AdminApi {
    /// Walks the audit chain from its first link.
    @GET("/api/admin/audit/verify")
    void verifyAudit(OnComplete<Response<VerificationDto>> callback);

    /// Checks every entry sums to zero in each asset and every balance is the
    /// sum of its postings.
    @GET("/api/admin/ledger/verify")
    void verifyLedger(OnComplete<Response<VerificationDto>> callback);

    /// Closes the books through a past time: revenue and expense move to
    /// retained earnings and nothing may be posted at or before it again.
    @POST("/api/admin/periods/close")
    void closePeriod(@Body ClosePeriodDto request, OnComplete<Response<PeriodDto>> callback);

    @POST("/api/admin/adjustments")
    void adjust(@Header("Idempotency-Key") String idempotencyKey, @Body AdjustmentDto request,
            OnComplete<Response<ApprovalDto>> callback);

    /// Corrects an entry the only way a ledger may: by posting its mirror
    /// image. The entry itself is never changed.
    @POST("/api/admin/journal/{id}/reverse")
    void reverse(@Path("id") String id, @Header("Idempotency-Key") String idempotencyKey,
            @Body ReversalRequestDto request, OnComplete<Response<ApprovalDto>> callback);

    /// Newest first: the pending ones, and the latest decided.
    @GET("/api/admin/approvals")
    void approvals(OnComplete<Response<List<ApprovalDto>>> callback);

    @POST("/api/admin/approvals/{id}/approve")
    void approve(@Path("id") String id, @Body DecisionDto decision,
            OnComplete<Response<ApprovalDto>> callback);

    @POST("/api/admin/approvals/{id}/reject")
    void reject(@Path("id") String id, @Body DecisionDto decision,
            OnComplete<Response<ApprovalDto>> callback);
}
