package com.example.staffmanagement.service.impl;

import com.example.staffmanagement.dto.StaffMajorFacilityDTO;
import com.example.staffmanagement.entity.MajorFacility;
import com.example.staffmanagement.entity.Staff;
import com.example.staffmanagement.entity.StaffMajorFacility;
import com.example.staffmanagement.exception.ResourceNotFoundException;
import com.example.staffmanagement.repository.MajorFacilityRepository;
import com.example.staffmanagement.repository.StaffMajorFacilityRepository;
import com.example.staffmanagement.repository.StaffRepository;
import com.example.staffmanagement.service.StaffMajorFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StaffMajorFacilityServiceImpl implements StaffMajorFacilityService {

    private final StaffMajorFacilityRepository staffMajorFacilityRepository;
    private final StaffRepository staffRepository;
    private final MajorFacilityRepository majorFacilityRepository;

    @Autowired
    public StaffMajorFacilityServiceImpl(
            StaffMajorFacilityRepository staffMajorFacilityRepository,
            StaffRepository staffRepository,
            MajorFacilityRepository majorFacilityRepository) {
        this.staffMajorFacilityRepository = staffMajorFacilityRepository;
        this.staffRepository = staffRepository;
        this.majorFacilityRepository = majorFacilityRepository;
    }

    @Override
    public List<StaffMajorFacilityDTO> getAllStaffMajorFacilities() {
        List<StaffMajorFacility> staffMajorFacilities = staffMajorFacilityRepository.findAll();
        return staffMajorFacilities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StaffMajorFacilityDTO getStaffMajorFacilityById(UUID id) {
        StaffMajorFacility staffMajorFacility = staffMajorFacilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên - Ngành học - Cơ sở", "id", id));
        return convertToDTO(staffMajorFacility);
    }

    @Override
    public List<StaffMajorFacilityDTO> getStaffMajorFacilitiesByStaff(UUID staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", staffId));

        List<StaffMajorFacility> staffMajorFacilities = staffMajorFacilityRepository.findByStaff(staff);
        return staffMajorFacilities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StaffMajorFacilityDTO> getStaffMajorFacilitiesByMajorFacility(UUID majorFacilityId) {
        MajorFacility majorFacility = majorFacilityRepository.findById(majorFacilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Ngành học - Cơ sở", "id", majorFacilityId));

        List<StaffMajorFacility> staffMajorFacilities = staffMajorFacilityRepository.findByMajorFacility(majorFacility);
        return staffMajorFacilities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StaffMajorFacilityDTO createStaffMajorFacility(StaffMajorFacilityDTO staffMajorFacilityDTO) {
        if (staffMajorFacilityDTO.getId() == null) {
            staffMajorFacilityDTO.setId(UUID.randomUUID());
        }

        Staff staff = staffRepository.findById(staffMajorFacilityDTO.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", staffMajorFacilityDTO.getStaffId()));

        MajorFacility majorFacility = majorFacilityRepository.findById(staffMajorFacilityDTO.getMajorFacilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Ngành học - Cơ sở", "id", staffMajorFacilityDTO.getMajorFacilityId()));

        if (existsByStaffAndMajorFacility(staff.getId(), majorFacility.getId())) {
            throw new IllegalArgumentException("Cặp nhân viên-ngành học-cơ sở đã tồn tại");
        }

        staffMajorFacilityDTO.setStatus((byte) 1);
        long currentTime = Instant.now().toEpochMilli();
        staffMajorFacilityDTO.setCreatedDate(currentTime);
        staffMajorFacilityDTO.setLastModifiedDate(currentTime);

        StaffMajorFacility staffMajorFacility = new StaffMajorFacility();
        staffMajorFacility.setId(staffMajorFacilityDTO.getId());
        staffMajorFacility.setStatus(staffMajorFacilityDTO.getStatus());
        staffMajorFacility.setCreatedDate(staffMajorFacilityDTO.getCreatedDate());
        staffMajorFacility.setLastModifiedDate(staffMajorFacilityDTO.getLastModifiedDate());
        staffMajorFacility.setStaff(staff);
        staffMajorFacility.setMajorFacility(majorFacility);

        staffMajorFacility = staffMajorFacilityRepository.save(staffMajorFacility);
        return convertToDTO(staffMajorFacility);
    }

    @Override
    public StaffMajorFacilityDTO updateStaffMajorFacility(UUID id, StaffMajorFacilityDTO staffMajorFacilityDTO) {
        StaffMajorFacility existingStaffMajorFacility = staffMajorFacilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên - Ngành học - Cơ sở", "id", id));

        Staff staff = staffRepository.findById(staffMajorFacilityDTO.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", staffMajorFacilityDTO.getStaffId()));

        MajorFacility majorFacility = majorFacilityRepository.findById(staffMajorFacilityDTO.getMajorFacilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Ngành học - Cơ sở", "id", staffMajorFacilityDTO.getMajorFacilityId()));

        boolean isStaffChanged = !existingStaffMajorFacility.getStaff().getId().equals(staff.getId());
        boolean isMajorFacilityChanged = !existingStaffMajorFacility.getMajorFacility().getId().equals(majorFacility.getId());

        if ((isStaffChanged || isMajorFacilityChanged)
                && existsByStaffAndMajorFacility(staff.getId(), majorFacility.getId())) {
            throw new IllegalArgumentException("Cặp nhân viên-ngành học-cơ sở đã tồn tại");
        }

        existingStaffMajorFacility.setStaff(staff);
        existingStaffMajorFacility.setMajorFacility(majorFacility);
        existingStaffMajorFacility.setLastModifiedDate(Instant.now().toEpochMilli());

        StaffMajorFacility updatedStaffMajorFacility = staffMajorFacilityRepository.save(existingStaffMajorFacility);
        return convertToDTO(updatedStaffMajorFacility);
    }

    @Override
    public void deleteStaffMajorFacility(UUID id) {
        StaffMajorFacility staffMajorFacility = staffMajorFacilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên - Ngành học - Cơ sở", "id", id));

        staffMajorFacility.setStatus((byte) 0);
        staffMajorFacility.setLastModifiedDate(Instant.now().toEpochMilli());
        staffMajorFacilityRepository.save(staffMajorFacility);
    }

    @Override
    public boolean existsByStaffAndMajorFacility(UUID staffId, UUID majorFacilityId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", staffId));

        MajorFacility majorFacility = majorFacilityRepository.findById(majorFacilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Ngành học - Cơ sở", "id", majorFacilityId));

        return staffMajorFacilityRepository.existsByStaffAndMajorFacility(staff, majorFacility);
    }

    private StaffMajorFacilityDTO convertToDTO(StaffMajorFacility staffMajorFacility) {
        StaffMajorFacilityDTO dto = new StaffMajorFacilityDTO();
        dto.setId(staffMajorFacility.getId());
        dto.setStatus(staffMajorFacility.getStatus());
        dto.setCreatedDate(staffMajorFacility.getCreatedDate());
        dto.setLastModifiedDate(staffMajorFacility.getLastModifiedDate());

        dto.setStaffId(staffMajorFacility.getStaff().getId());
        dto.setMajorFacilityId(staffMajorFacility.getMajorFacility().getId());

        dto.setStaffName(staffMajorFacility.getStaff().getName());
        dto.setMajorName(staffMajorFacility.getMajorFacility().getMajor().getName());
        dto.setFacilityName(staffMajorFacility.getMajorFacility().getDepartmentFacility().getFacility().getName());
        dto.setDepartmentName(staffMajorFacility.getMajorFacility().getDepartmentFacility().getDepartment().getName());

        return dto;
    }
}