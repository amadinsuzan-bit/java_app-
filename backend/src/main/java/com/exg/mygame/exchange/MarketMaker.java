package com.exg.mygame.exchange;

import com.codename1.backend.Config;
import com.codename1.backend.annotations.Component;
import com.codename1.backend.annotations.PostConstruct;
import com.codename1.backend.annotations.Scheduled;
import com.codename1.backend.annotations.Transactional;
import com.codename1.backend.annotations.Value;
import com.exg.mygame.Ids;
import com.exg.mygame.account.DemoAccounts;
import com.exg.mygame.chain.Chain;
import com.exg.mygame.api.NewOrderDto;
import com.exg.mygame.domain.Asset;
import com.exg.mygame.domain.LedgerAccount;
import com.exg.mygame.domain.Market;
import com.exg.mygame.domain.TradeOrder;
import com.exg.mygame.ledger.AccountKinds;
import com.exg.mygame.ledger.Ledger;
import com.exg.mygame.ledger.Leg;
import com.exg.mygame.repository.TradeOrderRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/// A simulated price feed and market maker, so a development server's
/// exchange has prices and a book to trade against.
///
/// Runs only on a development profile with `mygame.market-maker.enabled`
/// (true by default; the tests turn it off so their books are their own).
/// Every period it moves each market's index price a random step of up to
/// 0.3%, cancels its quotes and posts new ones: a ladder of bids below the
/// index and asks above it. It trades from `@market-maker`, an owner that is
/// not a username, through the same [Exchange] customers use -- it gets no
/// shortcut, its orders are held and audited like anyone's -- funded once
/// from SYS_CASH.
///
/// A production exchange gets its prices from real markets and its liquidity
/// from real market makers under contract; none of this ships.
@Component
public class MarketMaker {
    /// The maker's owner. A username is an e-mail address, so no customer
    /// can sign in as it.
    public static final String OWNER = "@market-maker";
    static final int LEVELS = 5;

    private final Exchange exchange;
    private final Markets markets;
    private final Ledger ledger;
    private final TradeOrderRepository orders;
    private final boolean enabled;
    // A price walk for a demo book, not a secret: java.util.Random, which a
    // compiled server's class library has (java.security does not exist there).
    private final Random random = new Random();

    /// `demo` is taken only so that it is built, and its history seeded,
    /// before this starts: that history funds the maker at its first day,
    /// and a float posted again today would be a day's volume of a million.
    private final Chain chain;

    public MarketMaker(Exchange exchange, Markets markets, Ledger ledger,
            TradeOrderRepository orders, Config config, DemoAccounts demo, Chain chain,
            @Value("${mygame.market-maker.enabled:true}") String enabled) {
        this.exchange = exchange;
        this.chain = chain;
        this.markets = markets;
        this.ledger = ledger;
        this.orders = orders;
        this.enabled = config.isDevelopmentProfile() && "true".equalsIgnoreCase(enabled.trim());
    }

    @PostConstruct
    public void start() throws IOException {
        if (!enabled) {
            return;
        }
        markets.seed();
        for (Market m : markets.all()) {
            fund(m.getQuote(), AccountKinds.CHECKING);
            if (!chain.handles(m.getBase())) {
                // A real coin is never minted, not even for the maker: it
                // quotes only what it bought.
                fund(m.getBase(), AccountKinds.WALLET);
            }
        }
        quote();
    }

    @Scheduled(fixedRateString = "${mygame.market-maker.period-ms:15000}")
    public void tick() throws IOException {
        if (!enabled) {
            return;
        }
        for (Market m : markets.all()) {
            long step = m.getIndexPrice() * (random.nextInt(61) - 30) / 10000L;
            markets.index(m.getId(), m.getIndexPrice() + step);
        }
        quote();
    }

    /// Replaces the maker's quotes on every market around its index price.
    public void quote() throws IOException {
        for (TradeOrder o : open()) {
            exchange.cancel(OWNER, o.getId());
        }
        for (Market m : markets.all()) {
            Asset base = ledger.asset(m.getBase());
            long scale = Markets.scale(base);
            // About 50.00 of quote at each level, in whole lots.
            long lots = Math.max(1L, 5000L * scale / Math.max(1L, m.getIndexPrice()) / m.getLot());
            for (int level = 1; level <= LEVELS; level++) {
                long spread = m.getIndexPrice() * (10L * level) / 10000L;
                place(m, Exchange.BUY, m.getIndexPrice() - spread, lots * m.getLot() * level);
                place(m, Exchange.SELL, m.getIndexPrice() + spread, lots * m.getLot() * level);
            }
        }
    }

    private void place(Market m, String side, long price, long quantity) throws IOException {
        if (price <= 0) {
            return;
        }
        try {
            exchange.place(OWNER, "maker-" + Ids.next(), new NewOrderDto(m.getId(), side,
                    Exchange.LIMIT, price, quantity, 0L));
        } catch (RuntimeException spent) {
            // Out of one side's money after customers bought or sold into it:
            // the book is thinner until the next deposit, not broken.
            System.err.println("mygame: market maker did not quote " + m.getId() + " " + side
                    + ": " + spent.getMessage());
        }
    }

    @Transactional(readOnly = true)
    private List<TradeOrder> open() throws IOException {
        return new ArrayList<TradeOrder>(
                orders.findByOwnerAndStatusOrderByCreatedAtDesc(OWNER, Exchange.OPEN));
    }

    /// One deposit per asset, ever: the fixed idempotency key makes a restart
    /// find the first.
    @Transactional(rollbackFor = Exception.class)
    private void fund(String asset, String kind) throws IOException {
        LedgerAccount account = null;
        for (LedgerAccount a : ledger.accountsOf(OWNER)) {
            if (a.getAsset().equals(asset) && a.getKind().equals(kind)) {
                account = a;
            }
        }
        if (account == null) {
            account = ledger.open(OWNER, asset, kind, "market maker");
        } else if (account.getBalance() > 0) {
            // Funded already: by the demo history, or by an earlier start.
            return;
        }
        Asset a = ledger.asset(asset);
        long amount = "FIAT".equals(a.getKind()) ? 100000000L : 100L * Markets.scale(a);
        LedgerAccount cash = ledger.system(asset, AccountKinds.SYS_CASH);
        List<Leg> legs = new ArrayList<Leg>();
        legs.add(new Leg(cash.getId(), -amount));
        legs.add(new Leg(account.getId(), amount));
        ledger.post("market-maker-fund-" + asset, "DEPOSIT", OWNER, "market maker float", legs);
    }
}
