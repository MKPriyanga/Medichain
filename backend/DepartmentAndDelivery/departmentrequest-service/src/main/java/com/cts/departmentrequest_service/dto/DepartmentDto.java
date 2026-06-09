
package com.cts.departmentrequest_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentDto {

    private Integer departmentId;
    private String name;
    private String status;
    private String contactInfo;
    private Integer headId;
}
