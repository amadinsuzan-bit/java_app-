package com.exg.mygame.repository;

import com.codename1.orm.repository.CrudRepository;
import com.codename1.orm.repository.Modifying;
import com.codename1.orm.repository.Param;
import com.codename1.orm.repository.Query;
import com.codename1.orm.repository.Repository;
import com.exg.mygame.domain.AuditHead;

/// The tail of the audit chain.
@Repository(entity = AuditHead.class, key = Integer.class)
public interface AuditHeadRepository extends CrudRepository<AuditHead, Integer> {
    Integer ONLY = Integer.valueOf(1);

    /// Writes the row without changing it, which holds it until the
    /// transaction ends.
    @Modifying
    @Query("update AuditHead h set h.seq = h.seq where h.id = 1")
    int hold();

    @Modifying
    @Query("update AuditHead h set h.seq = :seq, h.hash = :hash where h.id = 1")
    int advance(@Param("seq") long seq, @Param("hash") String hash);
}
