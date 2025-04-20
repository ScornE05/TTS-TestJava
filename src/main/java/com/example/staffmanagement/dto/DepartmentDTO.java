package com.example.staffmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO extends BaseDTO {

    @NotBlank(message = "Không được bỏ trống")
    private String code;

    @NotBlank(message = "Không được bỏ trống")
    private String name;
}