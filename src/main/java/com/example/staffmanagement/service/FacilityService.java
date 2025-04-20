package com.example.staffmanagement.service;

import com.example.staffmanagement.dto.FacilityDTO;

import java.util.List;
import java.util.UUID;

public interface FacilityService {
    List<FacilityDTO> getAllFacilities();
    FacilityDTO getFacilityById(UUID id);
    FacilityDTO getFacilityByCode(String code);
    FacilityDTO createFacility(FacilityDTO facilityDTO);
    FacilityDTO updateFacility(UUID id, FacilityDTO facilityDTO);
    void deleteFacility(UUID id);
    boolean isFacilityCodeExists(String code);
}