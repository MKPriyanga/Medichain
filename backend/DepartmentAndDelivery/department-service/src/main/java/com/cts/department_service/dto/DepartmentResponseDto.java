// package com.cts.department_service.dto;
//
//public class DepartmentResponseDto {
//	
//    private Integer departmentId;
//    private String name;
//    private Integer headId;
//    private String contactInfo;
//    private String status;
//    
//	public Integer getDepartmentId() {
//		return departmentId;
//	}
//	public void setDepartmentId(Integer departmentId) {
//		this.departmentId = departmentId;
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public Integer getHeadId() {
//		return headId;
//	}
//	public void setHeadId(Integer headId) {
//		this.headId = headId;
//	}
//	public String getContactInfo() {
//		return contactInfo;
//	}
//	public void setContactInfo(String contactInfo) {
//		this.contactInfo = contactInfo;
//	}
//	public String getStatus() {
//		return status;
//	}
//	public void setStatus(String status) {
//		this.status = status;
//	}
//
//}
package com.cts.department_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponseDto {

    private Integer departmentId;
    private String name;
    private Integer headId;
    private String contactInfo;
    private String status;
}

