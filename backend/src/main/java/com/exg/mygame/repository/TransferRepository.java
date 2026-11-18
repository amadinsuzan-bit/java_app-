package com.exg.mygame.repository;

import java.time.Instant;
import com.codename1.orm.repository.CrudRepository;
import com.codename1.orm.repository.Param;
import com.codename1.orm.repository.Query;
import com.codename1.orm.repository.Repository;
import com.exg.mygame.domain.Transfer;


/// Movements as the limits count them. Only ever inserted.
@Repository(entity = Transfer.class, key = String.class)
public interface TransferRepository extends CrudRepository<Transfer, String> {
    /// What `owner` has sent out of the bank or to someone else in `asset`
    /// since `since`; null when nothing.
    @Query("select sum(t.amount) from Transfer t where t.owner = :owner and t.asset = :asset "
            + "and t.outgoing = TRUE and t.createdAt >= :since")
    Long sentSince(@Param("owner") String owner, @Param("asset") String asset,
            @Param("since") Instant since);

    /// What has gone through a payee or a card since `since`; null when nothing.
    @Query("select sum(t.amount) from Transfer t where t.via = :via and t.createdAt >= :since")
    Long throughSince(@Param("via") String via, @Param("since") Instant since);

    /// What has gone through a payee in `asset` since `since`; null when nothing.
    @Query("select sum(t.amount) from Transfer t where t.via = :via and t.asset = :asset "
            + "and t.createdAt >= :since")
    Long throughSinceIn(@Param("via") String via, @Param("asset") String asset,
            @Param("since") Instant since);
}
