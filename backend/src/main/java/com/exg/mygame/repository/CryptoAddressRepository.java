package com.exg.mygame.repository;

import com.codename1.orm.repository.CrudRepository;
import com.codename1.orm.repository.Repository;
import com.exg.mygame.domain.CryptoAddress;

import java.util.List;

@Repository(entity = CryptoAddress.class, key = String.class)
public interface CryptoAddressRepository extends CrudRepository<CryptoAddress, String> {
    List<CryptoAddress> findByOwnerOrderByCreatedAtAsc(String owner);
}
