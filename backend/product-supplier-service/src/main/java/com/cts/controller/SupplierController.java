package com.cts.controller;


import jakarta.validation.Valid;


import org.springframework.web.bind.annotation.*;

import com.cts.entity.Supplier;
import com.cts.service.SupplierService;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    //@PreAuthorize("hasAnyRole('Admin','PROCUREMENT')")
    @GetMapping
    public List<Supplier> listSuppliers() {
        return supplierService.getAllSuppliers();
    }

    //@PreAuthorize("hasRole('Admin')")
    @PostMapping
    public Supplier createSupplier(@Valid @RequestBody Supplier supplier) {
        return supplierService.saveSupplier(supplier);
    }
}

