package com.example.staffmanagement.service;

import com.example.staffmanagement.dto.DepartmentDTO;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    List<DepartmentDTO> getAllDepartments();
    DepartmentDTO getDepartmentById(UUID id);
    DepartmentDTO getDepartmentByCode(String code);
    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);
    DepartmentDTO updateDepartment(UUID id, DepartmentDTO departmentDTO);
    void deleteDepartment(UUID id);
    boolean isDepartmentCodeExists(String code);
}