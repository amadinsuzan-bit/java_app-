package com.exg.mygame.repository;

import java.time.Instant;
import com.codename1.orm.repository.CrudRepository;
import com.codename1.orm.repository.Modifying;
import com.codename1.orm.repository.Param;
import com.codename1.orm.repository.Query;
import com.codename1.orm.repository.Repository;
import com.exg.mygame.domain.ChainCursor;


@Repository(entity = ChainCursor.class, key = String.class)
public interface ChainCursorRepository extends CrudRepository<ChainCursor, String> {
    /// Takes or renews the worker lease: succeeds (1) when `holder` already
    /// has it or the last holder has not renewed since `staleBefore`. One
    /// statement, so two servers racing for it cannot both win.
    @Modifying
    @Query("update ChainCursor c set c.lastHash = :holder, c.updatedAt = :now "
            + "where c.chain = :chain and (c.lastHash = :holder or c.updatedAt < :staleBefore)")
    int lease(@Param("chain") String chain, @Param("holder") String holder,
            @Param("now") Instant now, @Param("staleBefore") Instant staleBefore);
}
