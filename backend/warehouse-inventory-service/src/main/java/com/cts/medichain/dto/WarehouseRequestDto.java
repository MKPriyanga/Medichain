package com.cts.medichain.dto;

import lombok.Data;

@Data
public class WarehouseRequestDto {
    private String name;
    private String location;
    private Integer capacity;
}
