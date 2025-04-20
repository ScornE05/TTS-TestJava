package com.example.staffmanagement.service;

import com.example.staffmanagement.dto.DepartmentFacilityDTO;
import com.example.staffmanagement.entity.Department;
import com.example.staffmanagement.entity.Facility;
import com.example.staffmanagement.entity.Staff;

import java.util.List;
import java.util.UUID;

public interface DepartmentFacilityService {
    List<DepartmentFacilityDTO> getAllDepartmentFacilities();
    DepartmentFacilityDTO getDepartmentFacilityById(UUID id);
    List<DepartmentFacilityDTO> getDepartmentFacilitiesByDepartment(UUID departmentId);
    List<DepartmentFacilityDTO> getDepartmentFacilitiesByFacility(UUID facilityId);
    List<DepartmentFacilityDTO> getDepartmentFacilitiesByStaff(UUID staffId);
    DepartmentFacilityDTO createDepartmentFacility(DepartmentFacilityDTO departmentFacilityDTO);
    DepartmentFacilityDTO updateDepartmentFacility(UUID id, DepartmentFacilityDTO departmentFacilityDTO);
    void deleteDepartmentFacility(UUID id);
    boolean existsByDepartmentAndFacility(UUID departmentId, UUID facilityId);
}