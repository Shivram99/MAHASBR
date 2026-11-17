package com.mahasbr.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mahasbr.entity.RegistryApiAuthConfigEntity;

@Repository
public interface RegistryApiAuthConfigRepository extends JpaRepository<RegistryApiAuthConfigEntity, Long> {

    Optional<RegistryApiAuthConfigEntity> findByServiceNameCode(String serviceNameCode);

    Optional<RegistryApiAuthConfigEntity> findByServiceNameCodeIgnoreCase(String serviceNameCode);

    List<RegistryApiAuthConfigEntity> findByActiveTrue();

    // The sync job writes only its own status columns, so it never overwrites edits
    // an admin makes on the settings page while a sync is running.

    @Transactional
    @Modifying
    @Query("UPDATE RegistryApiAuthConfigEntity c SET c.lastRunStatus = :status, c.lastRunMessage = :message, "
            + "c.lastRunAt = :at WHERE c.id = :id")
    int updateRunStatus(@Param("id") Long id, @Param("status") String status, @Param("message") String message,
            @Param("at") LocalDateTime at);

    @Transactional
    @Modifying
    @Query("UPDATE RegistryApiAuthConfigEntity c SET c.lastSyncedTo = :date WHERE c.id = :id")
    int updateLastSyncedTo(@Param("id") Long id, @Param("date") LocalDate date);

    @Transactional
    @Modifying
    @Query("UPDATE RegistryApiAuthConfigEntity c SET c.active = :active WHERE c.id = :id")
    int updateActive(@Param("id") Long id, @Param("active") Boolean active);
}
