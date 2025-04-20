package com.example.staffmanagement.service;

import com.example.staffmanagement.dto.MajorFacilityDTO;

import java.util.List;
import java.util.UUID;

public interface MajorFacilityService {
    List<MajorFacilityDTO> getAllMajorFacilities();
    MajorFacilityDTO getMajorFacilityById(UUID id);
    List<MajorFacilityDTO> getMajorFacilitiesByDepartmentFacility(UUID departmentFacilityId);
    List<MajorFacilityDTO> getMajorFacilitiesByMajor(UUID majorId);
    MajorFacilityDTO createMajorFacility(MajorFacilityDTO majorFacilityDTO);
    MajorFacilityDTO updateMajorFacility(UUID id, MajorFacilityDTO majorFacilityDTO);
    void deleteMajorFacility(UUID id);
    boolean existsByDepartmentFacilityAndMajor(UUID departmentFacilityId, UUID majorId);
}