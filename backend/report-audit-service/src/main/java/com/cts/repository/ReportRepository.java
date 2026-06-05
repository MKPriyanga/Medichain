package com.cts.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

	List<Report> findByGeneratedAtBetween(LocalDateTime start, LocalDateTime end);
	void deleteByFileName(String fileName);
	List<Report> findByScope(String scope);
}