package com.cts.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cts.entity.AuditPackage;

@Repository
public interface AuditRepository extends JpaRepository<AuditPackage, Long> {

    @Query("SELECT a FROM AuditPackage a WHERE a.periodStart >= :start AND a.periodEnd <= :end")
    List<AuditPackage> findByPeriod(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
}