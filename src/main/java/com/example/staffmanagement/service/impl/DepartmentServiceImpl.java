package com.example.staffmanagement.service.impl;

import com.example.staffmanagement.dto.DepartmentDTO;
import com.example.staffmanagement.entity.Department;
import com.example.staffmanagement.entity.DepartmentFacility;
import com.example.staffmanagement.entity.MajorFacility;
import com.example.staffmanagement.entity.StaffMajorFacility;
import com.example.staffmanagement.exception.ResourceNotFoundException;
import com.example.staffmanagement.repository.DepartmentFacilityRepository;
import com.example.staffmanagement.repository.DepartmentRepository;
import com.example.staffmanagement.repository.MajorFacilityRepository;
import com.example.staffmanagement.repository.StaffMajorFacilityRepository;
import com.example.staffmanagement.service.DepartmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentFacilityRepository departmentFacilityRepository;
    private final MajorFacilityRepository majorFacilityRepository;
    private final StaffMajorFacilityRepository staffMajorFacilityRepository;

    @Autowired
    public DepartmentServiceImpl(
            DepartmentRepository departmentRepository,
            DepartmentFacilityRepository departmentFacilityRepository,
            MajorFacilityRepository majorFacilityRepository,
            StaffMajorFacilityRepository staffMajorFacilityRepository) {
        this.departmentRepository = departmentRepository;
        this.departmentFacilityRepository = departmentFacilityRepository;
        this.majorFacilityRepository = majorFacilityRepository;
        this.staffMajorFacilityRepository = staffMajorFacilityRepository;
    }

    @Override
    public List<DepartmentDTO> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentDTO getDepartmentById(UUID id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban", "id", id));
        return convertToDTO(department);
    }

    @Override
    public DepartmentDTO getDepartmentByCode(String code) {
        Department department = departmentRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban", "mã", code));
        return convertToDTO(department);
    }

    @Override
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        if (departmentDTO.getId() == null) {
            departmentDTO.setId(UUID.randomUUID());
        }

        if (isDepartmentCodeExists(departmentDTO.getCode())) {
            throw new IllegalArgumentException("Mã phòng ban đã tồn tại: " + departmentDTO.getCode());
        }

        departmentDTO.setStatus((byte) 1);
        long currentTime = Instant.now().toEpochMilli();
        departmentDTO.setCreatedDate(currentTime);
        departmentDTO.setLastModifiedDate(currentTime);

        Department department = convertToEntity(departmentDTO);
        department = departmentRepository.save(department);
        return convertToDTO(department);
    }

    @Override
    @Transactional
    public DepartmentDTO updateDepartment(UUID id, DepartmentDTO departmentDTO) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban", "id", id));
        if (!existingDepartment.getCode().equals(departmentDTO.getCode())
                && isDepartmentCodeExists(departmentDTO.getCode())) {
            throw new IllegalArgumentException("Mã phòng ban đã tồn tại: " + departmentDTO.getCode());
        }

        String oldName = existingDepartment.getName();
        String oldCode = existingDepartment.getCode();
        existingDepartment.setName(departmentDTO.getName());
        existingDepartment.setCode(departmentDTO.getCode());
        existingDepartment.setStatus(departmentDTO.getStatus());
        existingDepartment.setLastModifiedDate(Instant.now().toEpochMilli());

        Department updatedDepartment = departmentRepository.save(existingDepartment);

        if (!oldName.equals(departmentDTO.getName()) || !oldCode.equals(departmentDTO.getCode())) {
            List<DepartmentFacility> relatedDeptFacilities = departmentFacilityRepository.findByDepartment(existingDepartment);
            if (!relatedDeptFacilities.isEmpty()) {

                for (DepartmentFacility df : relatedDeptFacilities) {
                    df.setLastModifiedDate(Instant.now().toEpochMilli());
                    departmentFacilityRepository.save(df);
                }
            }
        }

        return convertToDTO(updatedDepartment);
    }

    @Override
    @Transactional
    public void deleteDepartment(UUID id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban", "id", id));

        List<DepartmentFacility> deptFacilities = departmentFacilityRepository.findByDepartment(department);

        for (DepartmentFacility df : deptFacilities) {
            List<MajorFacility> majorFacilities = majorFacilityRepository.findByDepartmentFacility(df);

            for (MajorFacility mf : majorFacilities) {
                List<StaffMajorFacility> staffMajorFacilities = staffMajorFacilityRepository.findByMajorFacility(mf);
                staffMajorFacilityRepository.deleteAll(staffMajorFacilities);

                majorFacilityRepository.delete(mf);
            }
            departmentFacilityRepository.delete(df);
        }

        departmentRepository.delete(department);
    }

    @Override
    public boolean isDepartmentCodeExists(String code) {
        return departmentRepository.existsByCode(code);
    }
    private DepartmentDTO convertToDTO(Department department) {
        DepartmentDTO departmentDTO = new DepartmentDTO();
        BeanUtils.copyProperties(department, departmentDTO);
        return departmentDTO;
    }

    private Department convertToEntity(DepartmentDTO departmentDTO) {
        Department department = new Department();
        BeanUtils.copyProperties(departmentDTO, department);
        return department;
    }
}