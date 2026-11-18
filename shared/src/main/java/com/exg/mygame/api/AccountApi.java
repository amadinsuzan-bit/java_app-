package com.exg.mygame.api;

import com.codename1.annotations.rest.Body;
import com.codename1.annotations.rest.Header;
import com.codename1.annotations.rest.GET;
import com.codename1.annotations.rest.POST;
import com.codename1.annotations.rest.Path;
import com.codename1.annotations.rest.RestClient;
import com.codename1.io.rest.Response;
import com.codename1.util.OnComplete;

import java.util.List;

/// The account a customer signs in to: opening one, reading it, and the
/// second factor that protects it.
///
/// Signing in itself is not here. It is the OAuth 2 authorization-code flow
/// with PKCE against `/login` and `/oauth2/**`, and passkeys against
/// `/webauthn/**`: none of it is a JSON call with a token, so none of it
/// belongs in a REST contract.
@RestClient
public interface AccountApi {
    /// Opens an account, and a US dollar checking account with it. The one
    /// call here that takes no token.
    @POST("/api/account/register")
    void register(@Body RegisterDto request, OnComplete<Response<UserDto>> callback);

    @GET("/api/me")
    void me(OnComplete<Response<UserDto>> callback);

    @POST("/api/account/password")
    void changePassword(@Body PasswordChangeDto change, OnComplete<Response<UserDto>> callback);

    /// Starts adding an authenticator app. Nothing changes at sign-in until
    /// [#confirmTotp] proves the app was set up. Both take the password after
    /// a password-only sign-in ([TotpStartDto]).
    @POST("/api/account/mfa/totp")
    void startTotp(@Body TotpStartDto start, OnComplete<Response<TotpEnrollmentDto>> callback);

    /// Finishes adding the app with a code it shows, and answers the recovery
    /// codes.
    @POST("/api/account/mfa/totp/confirm")
    void confirmTotp(@Body TotpCodeDto code, OnComplete<Response<RecoveryCodesDto>> callback);

    /// Replaces the recovery codes. Takes a current code from the app, so a
    /// stolen token alone cannot mint a way past the second factor.
    @POST("/api/account/mfa/recovery-codes")
    void newRecoveryCodes(@Body TotpCodeDto code,
            OnComplete<Response<RecoveryCodesDto>> callback);

    /// The account's passkeys. Adding one is a WebAuthn ceremony against the
    /// signed-in session (`/webauthn/register`), not a call here.
    @GET("/api/account/passkeys")
    void passkeys(OnComplete<Response<List<PasskeyDto>>> callback);

    /// Removes a passkey and answers the ones left. Signed by a device of the
    /// account or, with none, carrying the password ([PasskeyRemoveDto]).
    @POST("/api/account/passkeys/{id}/remove")
    void removePasskey(@Path("id") String id, @Header("MyGame-Signature") String signature,
            @Body PasskeyRemoveDto request, OnComplete<Response<List<PasskeyDto>>> callback);
}
