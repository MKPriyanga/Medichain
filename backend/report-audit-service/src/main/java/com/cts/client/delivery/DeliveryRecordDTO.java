package com.cts.client.delivery;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRecordDTO {

    private Integer deliveryId;
    private Integer requestId;
    private String deliveredBy;
    private LocalDateTime deliveredAt;
    private Integer quantity;
    private String status;   
}