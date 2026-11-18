package com.exg.mygame.repository;

import com.codename1.orm.repository.CrudRepository;
import com.codename1.orm.repository.Param;
import com.codename1.orm.repository.Query;
import com.codename1.orm.repository.Repository;
import com.exg.mygame.domain.AuditEvent;

import java.util.List;

/// The audit chain.
@Repository(entity = AuditEvent.class, key = Long.class)
public interface AuditEventRepository extends CrudRepository<AuditEvent, Long> {
    List<AuditEvent> findTop100ByActorOrderBySeqDesc(String actor);

    @Query("select e from AuditEvent e where e.seq > :after order by e.seq asc")
    List<AuditEvent> after(@Param("after") long after);

    /// One page of the chain, for walking it without loading it whole.
    List<AuditEvent> findTop500BySeqGreaterThanOrderBySeqAsc(long seq);
}
