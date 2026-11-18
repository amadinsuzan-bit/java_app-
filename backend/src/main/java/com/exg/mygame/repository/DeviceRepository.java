package com.exg.mygame.repository;

import com.codename1.orm.repository.CrudRepository;
import com.codename1.orm.repository.Modifying;
import com.codename1.orm.repository.Param;
import com.codename1.orm.repository.Query;
import com.codename1.orm.repository.Repository;
import com.exg.mygame.domain.Device;

import java.util.List;

/// The devices allowed to sign for a customer.
@Repository(entity = Device.class, key = String.class)
public interface DeviceRepository extends CrudRepository<Device, String> {
    List<Device> findByOwnerOrderByCreatedAtAsc(String owner);

    @Modifying
    @Query("update Device d set d.revoked = TRUE where d.id = :id and d.owner = :owner")
    int revoke(@Param("id") String id, @Param("owner") String owner);
}
