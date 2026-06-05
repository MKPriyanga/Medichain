package com.cts.service;


import org.springframework.stereotype.Service;

import com.cts.entity.Product;
import com.cts.exception.InvalidRequestException;
import com.cts.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepo;

    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product getProductById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product saveProduct(Product product) {

        if (product.getPrice() == null || product.getPrice() < 0) {
            throw new InvalidRequestException("Product price must be >= 0");
        }

        product.setStatus("ACTIVE");
        return productRepo.save(product);
    }
}

