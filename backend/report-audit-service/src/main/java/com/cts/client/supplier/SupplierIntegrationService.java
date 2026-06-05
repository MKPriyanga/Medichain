package com.cts.client.supplier;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SupplierIntegrationService {

    private final SupplierOrderClient supplierOrderClient;

    public SupplierIntegrationService(SupplierOrderClient supplierOrderClient) {
        this.supplierOrderClient = supplierOrderClient;
    }

    public List<SupplierOrderDTO> getAllRemoteOrders() {
        return supplierOrderClient.getAllOrders();
    }

    public List<SupplierDTO> getAllRemoteSuppliers() {
        return supplierOrderClient.getAllSuppliers();
    }
}