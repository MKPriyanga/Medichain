package com.cts.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank(message = "Product name is required")
    private String name;

    private String category;
    private String unit;

    @Min(value = 0, message = "Price must be zero or positive")
    private Double price;

    private String status; // ACTIVE / INACTIVE
}
