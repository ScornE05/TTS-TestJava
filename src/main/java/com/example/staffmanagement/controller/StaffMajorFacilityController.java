package com.example.staffmanagement.controller;

import com.example.staffmanagement.dto.StaffMajorFacilityDTO;
import com.example.staffmanagement.service.StaffMajorFacilityService;
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
@RequestMapping("/staff-major-facilities")
@Tag(name = "Quản lý nhân viên - ngành học - cơ sở", description = "API quản lý mối quan hệ giữa nhân viên, ngành học và cơ sở")
public class StaffMajorFacilityController {

    private final StaffMajorFacilityService staffMajorFacilityService;

    @Autowired
    public StaffMajorFacilityController(StaffMajorFacilityService staffMajorFacilityService) {
        this.staffMajorFacilityService = staffMajorFacilityService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả mối quan hệ giữa nhân viên, ngành học và cơ sở")
    public ResponseEntity<List<StaffMajorFacilityDTO>> getAllStaffMajorFacilities() {
        return ResponseEntity.ok(staffMajorFacilityService.getAllStaffMajorFacilities());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin mối quan hệ giữa nhân viên, ngành học và cơ sở theo ID")
    public ResponseEntity<StaffMajorFacilityDTO> getStaffMajorFacilityById(@PathVariable UUID id) {
        return ResponseEntity.ok(staffMajorFacilityService.getStaffMajorFacilityById(id));
    }

    @GetMapping("/staff/{staffId}")
    @Operation(summary = "Lấy danh sách mối quan hệ giữa nhân viên, ngành học và cơ sở theo ID nhân viên")
    public ResponseEntity<List<StaffMajorFacilityDTO>> getStaffMajorFacilitiesByStaff(@PathVariable UUID staffId) {
        return ResponseEntity.ok(staffMajorFacilityService.getStaffMajorFacilitiesByStaff(staffId));
    }

    @GetMapping("/major-facility/{majorFacilityId}")
    @Operation(summary = "Lấy danh sách mối quan hệ giữa nhân viên, ngành học và cơ sở theo ID ngành học-cơ sở")
    public ResponseEntity<List<StaffMajorFacilityDTO>> getStaffMajorFacilitiesByMajorFacility(@PathVariable UUID majorFacilityId) {
        return ResponseEntity.ok(staffMajorFacilityService.getStaffMajorFacilitiesByMajorFacility(majorFacilityId));
    }

    @PostMapping
    @Operation(summary = "Tạo mới mối quan hệ giữa nhân viên, ngành học và cơ sở",
            responses = @ApiResponse(responseCode = "201", description = "Mối quan hệ đã được tạo thành công"))
    public ResponseEntity<StaffMajorFacilityDTO> createStaffMajorFacility(@Valid @RequestBody StaffMajorFacilityDTO staffMajorFacilityDTO) {
        return new ResponseEntity<>(staffMajorFacilityService.createStaffMajorFacility(staffMajorFacilityDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin mối quan hệ giữa nhân viên, ngành học và cơ sở")
    public ResponseEntity<StaffMajorFacilityDTO> updateStaffMajorFacility(
            @PathVariable UUID id,
            @Valid @RequestBody StaffMajorFacilityDTO staffMajorFacilityDTO) {
        return ResponseEntity.ok(staffMajorFacilityService.updateStaffMajorFacility(id, staffMajorFacilityDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mối quan hệ giữa nhân viên, ngành học và cơ sở")
    public ResponseEntity<Void> deleteStaffMajorFacility(@PathVariable UUID id) {
        staffMajorFacilityService.deleteStaffMajorFacility(id);
        return ResponseEntity.noContent().build();
    }
}