package com.cts.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.entity.KPI;


@Repository
public interface KPIRepository extends JpaRepository<KPI, Long> {
    List<KPI> findByCategory(String category);
 // Add to KPIRepository.java
    Optional<KPI> findByName(String name);

}