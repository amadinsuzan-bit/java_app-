package com.exg.mygame.ui;

import com.codename1.components.SpanLabel;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.exg.mygame.security.Keys;

/// Turning on the two ways to unlock, shared by onboarding and Settings.
public final class UnlockSetup {
    private UnlockSetup() {
    }

    /// Stores the biometric copy of the app key; on Android the OS asks for
    /// the biometric to write it.
    public static void biometric(final Runnable done, final SpanLabel error) {
        Keys.enableBiometric(Lang.tr("Let your biometric unlock MyGame"), done,
                why -> Ui.showError(error, Lang.tr(why)));
    }

    /// A new PIN, typed twice.
    public static final class PinFields {
        public final TextField pin = Ui.field("New PIN", TextArea.NUMERIC | TextArea.PASSWORD);
        public final TextField again = Ui.field("The same PIN again",
                TextArea.NUMERIC | TextArea.PASSWORD);

        public PinFields() {
            pin.setMaxSize(Keys.MAX_PIN);
            again.setMaxSize(Keys.MAX_PIN);
        }

        /// Checks the two and stores the PIN copy of the app key.
        public void save(final Runnable done, final SpanLabel error) {
            String typed = pin.getText();
            if (!typed.equals(again.getText())) {
                Ui.showError(error, Lang.tr("The two PINs are not the same."));
                return;
            }
            if (!Keys.validPin(typed)) {
                Ui.showError(error, Lang.tr("Choose 6 to 12 digits that are not a simple run."));
                return;
            }
            Keys.setPin(typed, () -> {
                pin.setText("");
                again.setText("");
                done.run();
            }, why -> Ui.showError(error, Lang.tr(why)));
        }
    }
}
