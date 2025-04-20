package com.example.staffmanagement.controller;

import com.example.staffmanagement.dto.StaffDTO;
import com.example.staffmanagement.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/staffs")
@Tag(name = "Quản lý nhân viên", description = "API quản lý nhân viên")
public class StaffController {

    private final StaffService staffService;

    @Autowired
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả nhân viên")
    public ResponseEntity<List<StaffDTO>> getAllStaffs() {
        return ResponseEntity.ok(staffService.getAllStaffs());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin nhân viên theo ID")
    public ResponseEntity<StaffDTO> getStaffById(@PathVariable UUID id) {
        return ResponseEntity.ok(staffService.getStaffById(id));
    }

    @GetMapping("/code/{staffCode}")
    @Operation(summary = "Lấy thông tin nhân viên theo mã nhân viên")
    public ResponseEntity<StaffDTO> getStaffByStaffCode(@PathVariable String staffCode) {
        return ResponseEntity.ok(staffService.getStaffByStaffCode(staffCode));
    }

    @PostMapping
    @Operation(summary = "Tạo mới nhân viên",
            responses = @ApiResponse(responseCode = "201", description = "Nhân viên đã được tạo thành công"))
    public ResponseEntity<StaffDTO> createStaff(@Valid @RequestBody StaffDTO staffDTO) {
        return new ResponseEntity<>(staffService.createStaff(staffDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin nhân viên")
    public ResponseEntity<StaffDTO> updateStaff(@PathVariable UUID id, @Valid @RequestBody StaffDTO staffDTO) {
        return ResponseEntity.ok(staffService.updateStaff(id, staffDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa nhân viên")
    public ResponseEntity<Void> deleteStaff(@PathVariable UUID id) {
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Đổi trạng thái nhân viên (hoạt động/không hoạt động)")
    public ResponseEntity<StaffDTO> toggleStaffStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(staffService.toggleStaffStatus(id));
    }
}