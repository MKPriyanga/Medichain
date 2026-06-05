package com.cts.departmentrequest_service.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentRequestResponseDto {

    private DepartmentDto department;
    private Integer requestId;
    private Integer departmentId;
    private String productIdsJson;
    private Integer quantity;
    private String status;
    private LocalDateTime requestedAt;
    private String approvedBy;
    private String createdBy;
    private Long createdByUserId;
    private Integer headId;
}
