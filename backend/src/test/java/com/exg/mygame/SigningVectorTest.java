package com.exg.mygame;

import com.exg.mygame.api.SignedRequest;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

/// The canonical text the server verifies, for the vector the app's
/// `RequestSignerTest` signs: the same request must give the same text on
/// both sides, byte for byte.
class SigningVectorTest {
    @Test
    void canonicalForm() throws Exception {
        String body = "{\"amount\":1500,\"memo\":\"rent\"}";
        byte[] digest = com.codename1.backend.Crypto.sha256(body.getBytes("UTF-8"));
        assertEquals("POST|/api/accounts/a1/withdraw|"
                + "27faf49a231437b9d9308db1799c1ab06d8665c087da17478d59acbefa20a125|"
                + "idem-0123456789abcdef|AAECAwQFBgcICQoLDA0ODw|1767225600000",
                SignedRequest.canonical("POST", "/api/accounts/a1/withdraw", digest,
                        "idem-0123456789abcdef", "AAECAwQFBgcICQoLDA0ODw", 1767225600000L));
        assertEquals("dev1", SignedRequest.field(
                "v1;device=dev1;ts=1;nonce=n;sig=s", "device"));
    }
}
