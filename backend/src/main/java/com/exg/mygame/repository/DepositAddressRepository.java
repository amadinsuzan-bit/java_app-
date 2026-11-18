package com.exg.mygame.repository;

import com.codename1.orm.repository.CrudRepository;
import com.codename1.orm.repository.Repository;
import com.exg.mygame.domain.DepositAddress;

import java.util.List;

@Repository(entity = DepositAddress.class, key = String.class)
public interface DepositAddressRepository extends CrudRepository<DepositAddress, String> {
    List<DepositAddress> findByOwnerAndAsset(String owner, String asset);
}
