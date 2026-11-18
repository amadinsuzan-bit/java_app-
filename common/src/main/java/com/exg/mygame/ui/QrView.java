package com.exg.mygame.ui;

import com.codename1.ui.CN;
import com.codename1.ui.Component;
import com.codename1.ui.Graphics;
import com.codename1.ui.geom.Dimension;

/// Draws a [QrCode]: black modules on white with the four-module quiet zone
/// scanners need, whatever the theme -- a QR code in dark-mode colours is
/// one many cameras cannot read.
public final class QrView extends Component {
    private static final int QUIET = 4;
    private final QrCode code;

    public QrView(QrCode code) {
        this.code = code;
        setUIID("BnQr");
    }

    @Override
    protected Dimension calcPreferredSize() {
        int side = Math.min(CN.convertToPixels(45), CN.getDisplayWidth() * 3 / 4);
        return new Dimension(side, side);
    }

    @Override
    public void paint(Graphics g) {
        int modules = code.size() + QUIET * 2;
        int side = Math.min(getWidth(), getHeight());
        int cell = Math.max(1, side / modules);
        int drawn = cell * modules;
        int left = getX() + (getWidth() - drawn) / 2;
        int top = getY() + (getHeight() - drawn) / 2;
        g.setColor(0xffffff);
        g.fillRect(left, top, drawn, drawn);
        g.setColor(0x000000);
        for (int y = 0; y < code.size(); y++) {
            for (int x = 0; x < code.size(); x++) {
                if (code.isDark(x, y)) {
                    g.fillRect(left + (x + QUIET) * cell, top + (y + QUIET) * cell, cell, cell);
                }
            }
        }
    }
}
