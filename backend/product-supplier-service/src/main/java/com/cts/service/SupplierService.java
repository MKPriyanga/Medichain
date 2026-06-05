package com.cts.service;


import org.springframework.stereotype.Service;

import com.cts.entity.Supplier;
import com.cts.exception.InvalidRequestException;
import com.cts.repository.SupplierRepository;

import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepo;

    public SupplierService(SupplierRepository supplierRepo) {
        this.supplierRepo = supplierRepo;
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepo.findAll();
    }

    public Supplier saveSupplier(Supplier supplier) {

        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new InvalidRequestException("Supplier name cannot be empty");
        }

        supplier.setStatus("ACTIVE");
        return supplierRepo.save(supplier);
    }
}
