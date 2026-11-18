package com.exg.mygame.repository;

import com.codename1.orm.repository.CrudRepository;
import com.codename1.orm.repository.Repository;
import com.exg.mygame.domain.Payee;

import java.util.List;

/// Payees, always read through their owner so one customer never sees
/// another's.
@Repository(entity = Payee.class, key = String.class)
public interface PayeeRepository extends CrudRepository<Payee, String> {
    List<Payee> findByOwnerOrderByCreatedAtAsc(String owner);

    Payee findFirstByOwnerAndUsername(String owner, String username);
}
