package com.example.staffmanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentFacilityDTO extends BaseDTO {

    @NotNull(message = "ID phòng ban không thể trống")
    private UUID departmentId;

    @NotNull(message = "ID cơ sở không thể trống")
    private UUID facilityId;

    @NotNull(message = "ID nhân viên không thể trống")
    private UUID staffId;

    private String departmentName;
    private String facilityName;
    private String staffName;
}