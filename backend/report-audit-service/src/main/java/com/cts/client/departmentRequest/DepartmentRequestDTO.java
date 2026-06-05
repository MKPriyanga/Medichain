package com.cts.client.departmentRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequestDTO {
	
	private Integer requestId;      
    private Integer departmentId;   
    private String productIdsJson;
    private Integer quantity;       
    private String requestedAt;
    private String approvedBy;
    private String status;
}
