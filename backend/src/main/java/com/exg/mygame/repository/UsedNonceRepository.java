package com.exg.mygame.repository;

import java.time.Instant;
import com.codename1.orm.repository.CrudRepository;
import com.codename1.orm.repository.Modifying;
import com.codename1.orm.repository.Param;
import com.codename1.orm.repository.Query;
import com.codename1.orm.repository.Repository;
import com.exg.mygame.domain.UsedNonce;


/// Nonces signatures have used. Inserted, never updated; purged once expired.
@Repository(entity = UsedNonce.class, key = String.class)
public interface UsedNonceRepository extends CrudRepository<UsedNonce, String> {
    @Modifying
    @Query("delete from UsedNonce n where n.expiresAt < :now")
    int purge(@Param("now") Instant now);
}
