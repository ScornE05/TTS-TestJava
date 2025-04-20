package com.example.staffmanagement.controller;

import com.example.staffmanagement.dto.DepartmentFacilityDTO;
import com.example.staffmanagement.service.DepartmentFacilityService;
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
@RequestMapping("/department-facilities")
@Tag(name = "Quản lý phòng ban - cơ sở", description = "API quản lý mối quan hệ giữa phòng ban và cơ sở")
public class DepartmentFacilityController {

    private final DepartmentFacilityService departmentFacilityService;

    @Autowired
    public DepartmentFacilityController(DepartmentFacilityService departmentFacilityService) {
        this.departmentFacilityService = departmentFacilityService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả mối quan hệ giữa phòng ban và cơ sở")
    public ResponseEntity<List<DepartmentFacilityDTO>> getAllDepartmentFacilities() {
        return ResponseEntity.ok(departmentFacilityService.getAllDepartmentFacilities());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin mối quan hệ giữa phòng ban và cơ sở theo ID")
    public ResponseEntity<DepartmentFacilityDTO> getDepartmentFacilityById(@PathVariable UUID id) {
        return ResponseEntity.ok(departmentFacilityService.getDepartmentFacilityById(id));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Lấy danh sách mối quan hệ giữa phòng ban và cơ sở theo ID phòng ban")
    public ResponseEntity<List<DepartmentFacilityDTO>> getDepartmentFacilitiesByDepartment(@PathVariable UUID departmentId) {
        return ResponseEntity.ok(departmentFacilityService.getDepartmentFacilitiesByDepartment(departmentId));
    }

    @GetMapping("/facility/{facilityId}")
    @Operation(summary = "Lấy danh sách mối quan hệ giữa phòng ban và cơ sở theo ID cơ sở")
    public ResponseEntity<List<DepartmentFacilityDTO>> getDepartmentFacilitiesByFacility(@PathVariable UUID facilityId) {
        return ResponseEntity.ok(departmentFacilityService.getDepartmentFacilitiesByFacility(facilityId));
    }

    @GetMapping("/staff/{staffId}")
    @Operation(summary = "Lấy danh sách mối quan hệ giữa phòng ban và cơ sở theo ID nhân viên")
    public ResponseEntity<List<DepartmentFacilityDTO>> getDepartmentFacilitiesByStaff(@PathVariable UUID staffId) {
        return ResponseEntity.ok(departmentFacilityService.getDepartmentFacilitiesByStaff(staffId));
    }

    @PostMapping
    @Operation(summary = "Tạo mới mối quan hệ giữa phòng ban và cơ sở",
            responses = @ApiResponse(responseCode = "201", description = "Mối quan hệ đã được tạo thành công"))
    public ResponseEntity<DepartmentFacilityDTO> createDepartmentFacility(@Valid @RequestBody DepartmentFacilityDTO departmentFacilityDTO) {
        return new ResponseEntity<>(departmentFacilityService.createDepartmentFacility(departmentFacilityDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin mối quan hệ giữa phòng ban và cơ sở")
    public ResponseEntity<DepartmentFacilityDTO> updateDepartmentFacility(
            @PathVariable UUID id,
            @Valid @RequestBody DepartmentFacilityDTO departmentFacilityDTO) {
        return ResponseEntity.ok(departmentFacilityService.updateDepartmentFacility(id, departmentFacilityDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mối quan hệ giữa phòng ban và cơ sở")
    public ResponseEntity<Void> deleteDepartmentFacility(@PathVariable UUID id) {
        departmentFacilityService.deleteDepartmentFacility(id);
        return ResponseEntity.noContent().build();
    }
}