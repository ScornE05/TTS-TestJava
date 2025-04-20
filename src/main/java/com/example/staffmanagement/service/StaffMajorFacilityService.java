package com.example.staffmanagement.service;

import com.example.staffmanagement.dto.StaffMajorFacilityDTO;

import java.util.List;
import java.util.UUID;

public interface StaffMajorFacilityService {
    List<StaffMajorFacilityDTO> getAllStaffMajorFacilities();
    StaffMajorFacilityDTO getStaffMajorFacilityById(UUID id);
    List<StaffMajorFacilityDTO> getStaffMajorFacilitiesByStaff(UUID staffId);
    List<StaffMajorFacilityDTO> getStaffMajorFacilitiesByMajorFacility(UUID majorFacilityId);
    StaffMajorFacilityDTO createStaffMajorFacility(StaffMajorFacilityDTO staffMajorFacilityDTO);
    StaffMajorFacilityDTO updateStaffMajorFacility(UUID id, StaffMajorFacilityDTO staffMajorFacilityDTO);
    void deleteStaffMajorFacility(UUID id);
    boolean existsByStaffAndMajorFacility(UUID staffId, UUID majorFacilityId);
}