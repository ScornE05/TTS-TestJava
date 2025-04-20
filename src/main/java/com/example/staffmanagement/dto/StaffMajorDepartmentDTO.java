package com.example.staffmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffMajorDepartmentDTO {
    private UUID staffId;
    private String staffName;
    private String staffCode;
    private UUID facilityId;
    private String facilityName;
    private UUID departmentId;
    private String departmentName;
    private UUID majorId;
    private String majorName;
}