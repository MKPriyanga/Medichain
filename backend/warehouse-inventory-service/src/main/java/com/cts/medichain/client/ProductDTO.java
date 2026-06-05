package com.cts.medichain.client;

import lombok.Data;

@Data
public class ProductDTO {
    private Long productId;
    private String name;
    private String category;
    private String unit;
    private Double price;
    private String status;
}