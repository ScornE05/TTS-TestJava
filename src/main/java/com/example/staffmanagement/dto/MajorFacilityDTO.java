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
public class MajorFacilityDTO extends BaseDTO {

    @NotNull(message = "ID của phòng ban-cơ sở không thể trống")
    private UUID departmentFacilityId;

    @NotNull(message = "ID ngành học không thể trống")
    private UUID majorId;

    private String departmentName;
    private String facilityName;
    private String majorName;
}