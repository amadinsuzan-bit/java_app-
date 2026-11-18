package com.exg.mygame.repository;

import java.time.Instant;
import com.codename1.orm.repository.CrudRepository;
import com.codename1.orm.repository.Modifying;
import com.codename1.orm.repository.Param;
import com.codename1.orm.repository.Query;
import com.codename1.orm.repository.Repository;
import com.exg.mygame.domain.LoginFailure;

@Repository(entity = LoginFailure.class, key = String.class)
public interface LoginFailureRepository extends CrudRepository<LoginFailure, String> {
    /// Starts a new window when the old one has run out.
    @Modifying
    @Query("update LoginFailure f set f.failures = 0, f.firstAt = :now "
            + "where f.username = :username and f.firstAt < :cutoff")
    int restartWindow(@Param("username") String username, @Param("now") Instant now,
            @Param("cutoff") Instant cutoff);

    /// One more failure, counted by the database: two at once are two.
    @Modifying
    @Query("update LoginFailure f set f.failures = f.failures + 1 where f.username = :username")
    int increment(@Param("username") String username);

    /// Locks the account when the count has reached `attempts`, and starts
    /// over; 1 when this call locked it.
    @Modifying
    @Query("update LoginFailure f set f.lockedUntil = :until, f.failures = 0, f.firstAt = :now "
            + "where f.username = :username and f.failures >= :attempts")
    int lockIfReached(@Param("username") String username, @Param("attempts") int attempts,
            @Param("now") Instant now, @Param("until") Instant until);
}
