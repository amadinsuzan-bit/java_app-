package com.exg.mygame.repository;

import com.codename1.orm.repository.CrudRepository;
import com.codename1.orm.repository.Modifying;
import com.codename1.orm.repository.Param;
import com.codename1.orm.repository.Query;
import com.codename1.orm.repository.Repository;
import com.exg.mygame.domain.Market;

import java.util.List;

@Repository(entity = Market.class, key = String.class)
public interface MarketRepository extends CrudRepository<Market, String> {
    List<Market> findByBaseAndQuoteOrderByIdAsc(String base, String quote);

    /// Writes the row without changing it, holding it until the transaction
    /// ends: the market's lock, as audit_head is the audit chain's.
    @Modifying
    @Query("update Market m set m.seq = m.seq where m.id = :id")
    int hold(@Param("id") String id);

    @Modifying
    @Query("update Market m set m.seq = m.seq + 1 where m.id = :id")
    int advance(@Param("id") String id);

    @Modifying
    @Query("update Market m set m.lastPrice = :price where m.id = :id")
    int traded(@Param("id") String id, @Param("price") long price);

    @Modifying
    @Query("update Market m set m.indexPrice = :price where m.id = :id")
    int index(@Param("id") String id, @Param("price") long price);
}
