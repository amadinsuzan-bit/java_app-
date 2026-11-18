package com.exg.mygame.ui;

import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.layouts.BoxLayout;
import com.exg.mygame.api.MarketDto;
import com.exg.mygame.api.TradeDto;
import com.exg.mygame.money.Amounts;
import com.exg.mygame.net.Api;
import com.exg.mygame.net.Net;
import java.util.List;

/// The exchange's front page: every market with its index and last price,
/// and the user's own recent trades.
///
/// The exchange trades on the same ledger as the bank. A filled order is a
/// ledger entry like a transfer -- the buyer's dollars and the seller's coins
/// change hands in one balanced posting, the fee to the bank's fee account --
/// so a trade can never leave money in neither place.
public final class ExchangeForm {
    private ExchangeForm() {
    }

    public static void show(final Runnable back) {
        Form form = Nav.form("Exchange", back);
        final Container content = new Container(BoxLayout.y());
        form.add(content);
        Ui.show(form, true);
        load(content, back);
    }

    private static void load(final Container content, final Runnable back) {
        Ui.loading(content);
        Api.exchange().markets(Net.to((List<MarketDto> markets) ->
                Api.exchange().trades(Net.to((List<TradeDto> trades) ->
                        fill(content, markets, trades, back),
                        (status, message) -> fill(content, markets, null, back))),
                (status, message) -> Ui.failed(content, message, () -> load(content, back))));
    }

    private static void fill(final Container content, List<MarketDto> markets,
            List<TradeDto> trades, final Runnable back) {
        content.removeAll();
        content.add(Ui.section("Markets"));
        if (markets.isEmpty()) {
            content.add(Ui.empty("No markets are open."));
        }
        for (final MarketDto market : markets) {
            long shown = market.lastPrice() > 0 ? market.lastPrice() : market.indexPrice();
            content.add(Ui.row(market.base() + " / " + market.quote(),
                    Lang.tr("Index {0}", Amounts.format(market.indexPrice(), market.quote())),
                    Amounts.format(shown, market.quote()), "BnRowValue",
                    () -> MarketForm.show(market, () -> show(back))));
        }
        if (trades != null) {
            content.add(Ui.section("Your trades"));
            if (trades.isEmpty()) {
                content.add(Ui.empty("You have not traded yet."));
            }
            for (TradeDto trade : trades) {
                content.add(MarketForm.tradeRow(trade, markets));
            }
        }
        content.revalidate();
    }
}
