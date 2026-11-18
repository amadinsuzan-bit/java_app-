package com.exg.mygame.repository;

import com.codename1.orm.repository.CrudRepository;
import com.codename1.orm.repository.Repository;
import com.exg.mygame.domain.Asset;

import java.util.List;

/// The assets the ledger knows. Seeded by V2__ledger.sql.
@Repository(entity = Asset.class, key = String.class)
public interface AssetRepository extends CrudRepository<Asset, String> {
    List<Asset> findByEnabledTrueOrderByCodeAsc();
}
