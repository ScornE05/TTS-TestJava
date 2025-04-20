package com.example.staffmanagement.controller;

import com.example.staffmanagement.dto.FacilityDTO;
import com.example.staffmanagement.service.FacilityService;
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
@RequestMapping("/facilities")
@Tag(name = "Quản lý cơ sở", description = "API quản lý cơ sở")
public class FacilityController {

    private final FacilityService facilityService;

    @Autowired
    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả cơ sở")
    public ResponseEntity<List<FacilityDTO>> getAllFacilities() {
        return ResponseEntity.ok(facilityService.getAllFacilities());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin cơ sở theo ID")
    public ResponseEntity<FacilityDTO> getFacilityById(@PathVariable UUID id) {
        return ResponseEntity.ok(facilityService.getFacilityById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Lấy thông tin cơ sở theo mã cơ sở")
    public ResponseEntity<FacilityDTO> getFacilityByCode(@PathVariable String code) {
        return ResponseEntity.ok(facilityService.getFacilityByCode(code));
    }

    @PostMapping
    @Operation(summary = "Tạo mới cơ sở",
            responses = @ApiResponse(responseCode = "201", description = "Cơ sở đã được tạo thành công"))
    public ResponseEntity<FacilityDTO> createFacility(@Valid @RequestBody FacilityDTO facilityDTO) {
        return new ResponseEntity<>(facilityService.createFacility(facilityDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin cơ sở")
    public ResponseEntity<FacilityDTO> updateFacility(@PathVariable UUID id, @Valid @RequestBody FacilityDTO facilityDTO) {
        return ResponseEntity.ok(facilityService.updateFacility(id, facilityDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa cơ sở")
    public ResponseEntity<Void> deleteFacility(@PathVariable UUID id) {
        facilityService.deleteFacility(id);
        return ResponseEntity.noContent().build();
    }
}