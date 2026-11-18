package com.exg.mygame.money;

import com.codename1.backend.annotations.Bean;
import com.codename1.backend.annotations.ConditionalOnMissingBean;
import com.codename1.backend.annotations.Configuration;
import com.codename1.backend.Config;
import java.io.IOException;

/// The pieces of money movement a deployment replaces.
@Configuration
public class MoneyConfig {
    /// The device integrity check. Simulated, and only on a development
    /// profile (`dev`, `test`, `local`): it believes whatever the device
    /// reports, so on any other profile the server refuses to start until
    /// the deployment declares its own [IntegrityVerifier] bean -- one that
    /// sends the Play Integrity token to Google's `decodeIntegrityToken` and
    /// checks an App Attest assertion against Apple's, see [IntegrityVerifier].
    /// Declaring that bean makes this one step aside.
    ///
    /// Setting `mygame.appattest.app-id` (`<teamId>.<bundleId>`) verifies
    /// iOS attestations for real with [AppAttestVerifier], anchored at Apple's
    /// App Attestation Root CA; `mygame.appattest.production=true` expects
    /// the production AAGUID instead of the development one. Every other
    /// platform stays with the simulated check.
    @Bean
    @ConditionalOnMissingBean(IntegrityVerifier.class)
    public IntegrityVerifier integrityVerifier(Config config) throws IOException {
        IntegrityVerifier simulated = SimulatedIntegrityVerifier.forProfile(config.isDevelopmentProfile());
        String appId = config.get("mygame.appattest.app-id", "");
        if (appId.length() == 0) {
            return simulated;
        }
        return AppAttestVerifier.apple(appId, config.getBoolean("mygame.appattest.production", false),
                simulated);
    }
}
