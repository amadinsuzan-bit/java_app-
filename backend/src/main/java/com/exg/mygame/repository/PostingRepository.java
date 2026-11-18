package com.exg.mygame.repository;

import com.codename1.orm.repository.CrudRepository;
import com.codename1.orm.repository.Param;
import com.codename1.orm.repository.Query;
import com.codename1.orm.repository.Repository;
import com.exg.mygame.domain.Posting;

import java.util.List;

/// The legs of every entry. Only ever inserted.
@Repository(entity = Posting.class, key = String.class)
public interface PostingRepository extends CrudRepository<Posting, String> {
    List<Posting> findByEntryId(String entryId);

    List<Posting> findTop100ByAccountIdOrderByCreatedAtDescIdDesc(String accountId);

    /// What the postings say an account holds; null when it has none.
    @Query("select sum(p.amount) from Posting p where p.accountId = :id")
    Long total(@Param("id") String accountId);

    /// What all the postings in one asset add up to. Zero, always, or the
    /// ledger is broken.
    @Query("select sum(p.amount) from Posting p where p.asset = :asset")
    Long assetTotal(@Param("asset") String asset);
}
