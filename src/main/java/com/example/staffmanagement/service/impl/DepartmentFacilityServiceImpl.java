package com.example.staffmanagement.service.impl;

import com.example.staffmanagement.dto.DepartmentFacilityDTO;
import com.example.staffmanagement.entity.Department;
import com.example.staffmanagement.entity.DepartmentFacility;
import com.example.staffmanagement.entity.Facility;
import com.example.staffmanagement.entity.Staff;
import com.example.staffmanagement.exception.ResourceNotFoundException;
import com.example.staffmanagement.repository.DepartmentFacilityRepository;
import com.example.staffmanagement.repository.DepartmentRepository;
import com.example.staffmanagement.repository.FacilityRepository;
import com.example.staffmanagement.repository.StaffRepository;
import com.example.staffmanagement.service.DepartmentFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DepartmentFacilityServiceImpl implements DepartmentFacilityService {

    private final DepartmentFacilityRepository departmentFacilityRepository;
    private final DepartmentRepository departmentRepository;
    private final FacilityRepository facilityRepository;
    private final StaffRepository staffRepository;

    @Autowired
    public DepartmentFacilityServiceImpl(
            DepartmentFacilityRepository departmentFacilityRepository,
            DepartmentRepository departmentRepository,
            FacilityRepository facilityRepository,
            StaffRepository staffRepository) {
        this.departmentFacilityRepository = departmentFacilityRepository;
        this.departmentRepository = departmentRepository;
        this.facilityRepository = facilityRepository;
        this.staffRepository = staffRepository;
    }

    @Override
    public List<DepartmentFacilityDTO> getAllDepartmentFacilities() {
        List<DepartmentFacility> departmentFacilities = departmentFacilityRepository.findAll();
        return departmentFacilities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentFacilityDTO getDepartmentFacilityById(UUID id) {
        DepartmentFacility departmentFacility = departmentFacilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban - Cơ sở", "id", id));
        return convertToDTO(departmentFacility);
    }

    @Override
    public List<DepartmentFacilityDTO> getDepartmentFacilitiesByDepartment(UUID departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban", "id", departmentId));

        List<DepartmentFacility> departmentFacilities = departmentFacilityRepository.findByDepartment(department);
        return departmentFacilities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentFacilityDTO> getDepartmentFacilitiesByFacility(UUID facilityId) {
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Cơ sở", "id", facilityId));

        List<DepartmentFacility> departmentFacilities = departmentFacilityRepository.findByFacility(facility);
        return departmentFacilities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentFacilityDTO> getDepartmentFacilitiesByStaff(UUID staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", staffId));

        List<DepartmentFacility> departmentFacilities = departmentFacilityRepository.findByStaff(staff);
        return departmentFacilities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentFacilityDTO createDepartmentFacility(DepartmentFacilityDTO departmentFacilityDTO) {
        if (departmentFacilityDTO.getId() == null) {
            departmentFacilityDTO.setId(UUID.randomUUID());
        }

        // Lấy các thực thể tham chiếu
        Department department = departmentRepository.findById(departmentFacilityDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban", "id", departmentFacilityDTO.getDepartmentId()));

        Facility facility = facilityRepository.findById(departmentFacilityDTO.getFacilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Cơ sở", "id", departmentFacilityDTO.getFacilityId()));

        Staff staff = staffRepository.findById(departmentFacilityDTO.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", departmentFacilityDTO.getStaffId()));
        if (existsByDepartmentAndFacility(department.getId(), facility.getId())) {
            throw new IllegalArgumentException("Cặp phòng ban-cơ sở đã tồn tại");
        }
        departmentFacilityDTO.setStatus((byte) 1);
        long currentTime = Instant.now().toEpochMilli();
        departmentFacilityDTO.setCreatedDate(currentTime);
        departmentFacilityDTO.setLastModifiedDate(currentTime);
        DepartmentFacility departmentFacility = new DepartmentFacility();
        departmentFacility.setId(departmentFacilityDTO.getId());
        departmentFacility.setStatus(departmentFacilityDTO.getStatus());
        departmentFacility.setCreatedDate(departmentFacilityDTO.getCreatedDate());
        departmentFacility.setLastModifiedDate(departmentFacilityDTO.getLastModifiedDate());
        departmentFacility.setDepartment(department);
        departmentFacility.setFacility(facility);
        departmentFacility.setStaff(staff);

        departmentFacility = departmentFacilityRepository.save(departmentFacility);
        return convertToDTO(departmentFacility);
    }

    @Override
    public DepartmentFacilityDTO updateDepartmentFacility(UUID id, DepartmentFacilityDTO departmentFacilityDTO) {
        DepartmentFacility existingDepartmentFacility = departmentFacilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban - Cơ sở", "id", id));

        Department department = departmentRepository.findById(departmentFacilityDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban", "id", departmentFacilityDTO.getDepartmentId()));

        Facility facility = facilityRepository.findById(departmentFacilityDTO.getFacilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Cơ sở", "id", departmentFacilityDTO.getFacilityId()));

        Staff staff = staffRepository.findById(departmentFacilityDTO.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", departmentFacilityDTO.getStaffId()));

        boolean isDepartmentChanged = !existingDepartmentFacility.getDepartment().getId().equals(department.getId());
        boolean isFacilityChanged = !existingDepartmentFacility.getFacility().getId().equals(facility.getId());

        if ((isDepartmentChanged || isFacilityChanged)
                && existsByDepartmentAndFacility(department.getId(), facility.getId())) {
            throw new IllegalArgumentException("Cặp phòng ban-cơ sở đã tồn tại");
        }

        existingDepartmentFacility.setDepartment(department);
        existingDepartmentFacility.setFacility(facility);
        existingDepartmentFacility.setStaff(staff);
        existingDepartmentFacility.setLastModifiedDate(Instant.now().toEpochMilli());

        DepartmentFacility updatedDepartmentFacility = departmentFacilityRepository.save(existingDepartmentFacility);
        return convertToDTO(updatedDepartmentFacility);
    }

    @Override
    public void deleteDepartmentFacility(UUID id) {
        DepartmentFacility departmentFacility = departmentFacilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban - Cơ sở", "id", id));

        departmentFacility.setStatus((byte) 0);
        departmentFacility.setLastModifiedDate(Instant.now().toEpochMilli());
        departmentFacilityRepository.save(departmentFacility);
    }

    @Override
    public boolean existsByDepartmentAndFacility(UUID departmentId, UUID facilityId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban", "id", departmentId));

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Cơ sở", "id", facilityId));

        return departmentFacilityRepository.existsByDepartmentAndFacility(department, facility);
    }

    private DepartmentFacilityDTO convertToDTO(DepartmentFacility departmentFacility) {
        DepartmentFacilityDTO dto = new DepartmentFacilityDTO();
        dto.setId(departmentFacility.getId());
        dto.setStatus(departmentFacility.getStatus());
        dto.setCreatedDate(departmentFacility.getCreatedDate());
        dto.setLastModifiedDate(departmentFacility.getLastModifiedDate());

        dto.setDepartmentId(departmentFacility.getDepartment().getId());
        dto.setFacilityId(departmentFacility.getFacility().getId());
        dto.setStaffId(departmentFacility.getStaff().getId());

        dto.setDepartmentName(departmentFacility.getDepartment().getName());
        dto.setFacilityName(departmentFacility.getFacility().getName());
        dto.setStaffName(departmentFacility.getStaff().getName());

        return dto;
    }
}