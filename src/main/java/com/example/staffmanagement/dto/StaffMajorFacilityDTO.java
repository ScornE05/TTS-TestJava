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
public class StaffMajorFacilityDTO extends BaseDTO {

    @NotNull(message = "ID của ngành học-cơ sở không thể trống")
    private UUID majorFacilityId;

    @NotNull(message = "ID nhân viên không thể trống")
    private UUID staffId;

    private String staffName;
    private String majorName;
    private String facilityName;
    private String departmentName;
}