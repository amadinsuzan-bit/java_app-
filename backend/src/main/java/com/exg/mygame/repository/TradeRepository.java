package com.exg.mygame.repository;

import java.time.Instant;
import com.codename1.orm.repository.CrudRepository;
import com.codename1.orm.repository.Param;
import com.codename1.orm.repository.Query;
import com.codename1.orm.repository.Repository;
import com.exg.mygame.domain.Trade;

import java.util.List;

@Repository(entity = Trade.class, key = String.class)
public interface TradeRepository extends CrudRepository<Trade, String> {
    @Query("select t from Trade t where t.buyer = :who or t.seller = :who "
            + "order by t.createdAt desc")
    List<Trade> of(@Param("who") String who);

    /// `who`'s fills, on either side, since `since`: what the daily limit
    /// counts of their trading.
    @Query("select t from Trade t where (t.buyer = :who or t.seller = :who) "
            + "and t.createdAt >= :since")
    List<Trade> since(@Param("who") String who, @Param("since") Instant since);
}
