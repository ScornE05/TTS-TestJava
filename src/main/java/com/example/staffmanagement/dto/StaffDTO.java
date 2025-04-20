package com.example.staffmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class StaffDTO extends BaseDTO {

    @NotBlank(message = "Email FE không được để trống")
    @Email(message = "Email FE phải đúng định dạng")
    @Size(max = 100, message = "Email FE không được vượt quá 100 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@fe\\.edu\\.vn$", message = "Email FE phải có định dạng @fe.edu.vn")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@.*$", message = "Email FE không được chứa khoảng trắng và ký tự đặc biệt")
    private String accountFe;

    @NotBlank(message = "Email FPT không được để trống")
    @Email(message = "Email FPT phải đúng định dạng")
    @Size(max = 100, message = "Email FPT không được vượt quá 100 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@fpt\\.edu\\.vn$", message = "Email FPT phải có định dạng @fpt.edu.vn")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@.*$", message = "Email FPT không được chứa khoảng trắng và ký tự đặc biệt")
    private String accountFpt;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 100, message = "Tên không được vượt quá 100 ký tự")
    private String name;

    @NotBlank(message = "Mã nhân viên không được để trống")
    @Size(max = 15, message = "Mã nhân viên không được vượt quá 15 ký tự")
    private String staffCode;
}