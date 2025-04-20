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
        // Tạo một UUID mới nếu không được cung cấp
        if (departmentDTO.getId() == null) {
            departmentDTO.setId(UUID.randomUUID());
        }

        // Kiểm tra xem mã phòng ban đã tồn tại chưa
        if (isDepartmentCodeExists(departmentDTO.getCode())) {
            throw new IllegalArgumentException("Mã phòng ban đã tồn tại: " + departmentDTO.getCode());
        }

        // Thiết lập trạng thái và thời gian
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

        // Kiểm tra xem mã phòng ban đã tồn tại chưa (nếu đã thay đổi)
        if (!existingDepartment.getCode().equals(departmentDTO.getCode())
                && isDepartmentCodeExists(departmentDTO.getCode())) {
            throw new IllegalArgumentException("Mã phòng ban đã tồn tại: " + departmentDTO.getCode());
        }

        // Lưu giữ thông tin cũ để đồng bộ cập nhật
        String oldName = existingDepartment.getName();
        String oldCode = existingDepartment.getCode();

        // Cập nhật thông tin
        existingDepartment.setName(departmentDTO.getName());
        existingDepartment.setCode(departmentDTO.getCode());
        existingDepartment.setStatus(departmentDTO.getStatus());
        existingDepartment.setLastModifiedDate(Instant.now().toEpochMilli());

        Department updatedDepartment = departmentRepository.save(existingDepartment);

        // Lấy danh sách DepartmentFacility liên quan và cập nhật đồng bộ (nếu cần)
        if (!oldName.equals(departmentDTO.getName()) || !oldCode.equals(departmentDTO.getCode())) {
            List<DepartmentFacility> relatedDeptFacilities = departmentFacilityRepository.findByDepartment(existingDepartment);
            if (!relatedDeptFacilities.isEmpty()) {
                // Cập nhật thông tin liên quan
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

        // Xóa các bản ghi liên quan theo thứ tự phân cấp để tránh lỗi ràng buộc khóa ngoại
        List<DepartmentFacility> deptFacilities = departmentFacilityRepository.findByDepartment(department);

        for (DepartmentFacility df : deptFacilities) {
            // Xóa các MajorFacility liên quan
            List<MajorFacility> majorFacilities = majorFacilityRepository.findByDepartmentFacility(df);

            for (MajorFacility mf : majorFacilities) {
                // Xóa các StaffMajorFacility liên quan
                List<StaffMajorFacility> staffMajorFacilities = staffMajorFacilityRepository.findByMajorFacility(mf);
                staffMajorFacilityRepository.deleteAll(staffMajorFacilities);

                // Xóa MajorFacility
                majorFacilityRepository.delete(mf);
            }

            // Xóa DepartmentFacility
            departmentFacilityRepository.delete(df);
        }

        // Cuối cùng xóa Department
        departmentRepository.delete(department);
    }

    @Override
    public boolean isDepartmentCodeExists(String code) {
        return departmentRepository.existsByCode(code);
    }

    // Phương thức chuyển đổi từ Entity sang DTO
    private DepartmentDTO convertToDTO(Department department) {
        DepartmentDTO departmentDTO = new DepartmentDTO();
        BeanUtils.copyProperties(department, departmentDTO);
        return departmentDTO;
    }

    // Phương thức chuyển đổi từ DTO sang Entity
    private Department convertToEntity(DepartmentDTO departmentDTO) {
        Department department = new Department();
        BeanUtils.copyProperties(departmentDTO, department);
        return department;
    }
}