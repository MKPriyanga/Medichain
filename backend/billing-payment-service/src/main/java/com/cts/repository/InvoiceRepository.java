package com.cts.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}