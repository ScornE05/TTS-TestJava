package com.example.staffmanagement.controller;

import com.example.staffmanagement.dto.MajorFacilityDTO;
import com.example.staffmanagement.service.MajorFacilityService;
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
@RequestMapping("/major-facilities")
@Tag(name = "Quản lý ngành học - cơ sở", description = "API quản lý mối quan hệ giữa ngành học và cơ sở")
public class MajorFacilityController {

    private final MajorFacilityService majorFacilityService;

    @Autowired
    public MajorFacilityController(MajorFacilityService majorFacilityService) {
        this.majorFacilityService = majorFacilityService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả mối quan hệ giữa ngành học và cơ sở")
    public ResponseEntity<List<MajorFacilityDTO>> getAllMajorFacilities() {
        return ResponseEntity.ok(majorFacilityService.getAllMajorFacilities());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin mối quan hệ giữa ngành học và cơ sở theo ID")
    public ResponseEntity<MajorFacilityDTO> getMajorFacilityById(@PathVariable UUID id) {
        return ResponseEntity.ok(majorFacilityService.getMajorFacilityById(id));
    }

    @GetMapping("/department-facility/{departmentFacilityId}")
    @Operation(summary = "Lấy danh sách mối quan hệ giữa ngành học và cơ sở theo ID phòng ban-cơ sở")
    public ResponseEntity<List<MajorFacilityDTO>> getMajorFacilitiesByDepartmentFacility(@PathVariable UUID departmentFacilityId) {
        return ResponseEntity.ok(majorFacilityService.getMajorFacilitiesByDepartmentFacility(departmentFacilityId));
    }

    @GetMapping("/major/{majorId}")
    @Operation(summary = "Lấy danh sách mối quan hệ giữa ngành học và cơ sở theo ID ngành học")
    public ResponseEntity<List<MajorFacilityDTO>> getMajorFacilitiesByMajor(@PathVariable UUID majorId) {
        return ResponseEntity.ok(majorFacilityService.getMajorFacilitiesByMajor(majorId));
    }

    @PostMapping
    @Operation(summary = "Tạo mới mối quan hệ giữa ngành học và cơ sở",
            responses = @ApiResponse(responseCode = "201", description = "Mối quan hệ đã được tạo thành công"))
    public ResponseEntity<MajorFacilityDTO> createMajorFacility(@Valid @RequestBody MajorFacilityDTO majorFacilityDTO) {
        return new ResponseEntity<>(majorFacilityService.createMajorFacility(majorFacilityDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin mối quan hệ giữa ngành học và cơ sở")
    public ResponseEntity<MajorFacilityDTO> updateMajorFacility(
            @PathVariable UUID id,
            @Valid @RequestBody MajorFacilityDTO majorFacilityDTO) {
        return ResponseEntity.ok(majorFacilityService.updateMajorFacility(id, majorFacilityDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mối quan hệ giữa ngành học và cơ sở")
    public ResponseEntity<Void> deleteMajorFacility(@PathVariable UUID id) {
        majorFacilityService.deleteMajorFacility(id);
        return ResponseEntity.noContent().build();
    }
}