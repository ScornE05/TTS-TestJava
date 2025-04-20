package com.example.staffmanagement.controller;

import com.example.staffmanagement.dto.MajorDTO;
import com.example.staffmanagement.service.MajorService;
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
@RequestMapping("/majors")
@Tag(name = "Quản lý ngành học", description = "API quản lý ngành học")
public class MajorController {

    private final MajorService majorService;

    @Autowired
    public MajorController(MajorService majorService) {
        this.majorService = majorService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả ngành học")
    public ResponseEntity<List<MajorDTO>> getAllMajors() {
        return ResponseEntity.ok(majorService.getAllMajors());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin ngành học theo ID")
    public ResponseEntity<MajorDTO> getMajorById(@PathVariable UUID id) {
        return ResponseEntity.ok(majorService.getMajorById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Lấy thông tin ngành học theo mã")
    public ResponseEntity<MajorDTO> getMajorByCode(@PathVariable String code) {
        return ResponseEntity.ok(majorService.getMajorByCode(code));
    }

    @PostMapping
    @Operation(summary = "Tạo mới ngành học",
            responses = @ApiResponse(responseCode = "201", description = "Ngành học đã được tạo thành công"))
    public ResponseEntity<MajorDTO> createMajor(@Valid @RequestBody MajorDTO majorDTO) {
        return new ResponseEntity<>(majorService.createMajor(majorDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin ngành học")
    public ResponseEntity<MajorDTO> updateMajor(@PathVariable UUID id, @Valid @RequestBody MajorDTO majorDTO) {
        return ResponseEntity.ok(majorService.updateMajor(id, majorDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa ngành học")
    public ResponseEntity<Void> deleteMajor(@PathVariable UUID id) {
        majorService.deleteMajor(id);
        return ResponseEntity.noContent().build();
    }
}