package com.exg.mygame.repository;

import com.codename1.orm.repository.CrudRepository;
import com.codename1.orm.repository.Repository;
import com.exg.mygame.domain.JournalEntry;

/// Movements of value, found again by the key their request carried.
@Repository(entity = JournalEntry.class, key = String.class)
public interface JournalEntryRepository extends CrudRepository<JournalEntry, String> {
    JournalEntry findFirstByIdempotencyKey(String idempotencyKey);
}
