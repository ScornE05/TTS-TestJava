package com.example.staffmanagement.service.impl;

import com.example.staffmanagement.dto.DepartmentDTO;
import com.example.staffmanagement.entity.Department;
import com.example.staffmanagement.exception.ResourceNotFoundException;
import com.example.staffmanagement.repository.DepartmentRepository;
import com.example.staffmanagement.service.DepartmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
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
    public DepartmentDTO updateDepartment(UUID id, DepartmentDTO departmentDTO) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban", "id", id));

        // Kiểm tra xem mã phòng ban đã tồn tại chưa (nếu đã thay đổi)
        if (!existingDepartment.getCode().equals(departmentDTO.getCode())
                && isDepartmentCodeExists(departmentDTO.getCode())) {
            throw new IllegalArgumentException("Mã phòng ban đã tồn tại: " + departmentDTO.getCode());
        }

        // Cập nhật thông tin
        existingDepartment.setName(departmentDTO.getName());
        existingDepartment.setCode(departmentDTO.getCode());
        existingDepartment.setLastModifiedDate(Instant.now().toEpochMilli());

        Department updatedDepartment = departmentRepository.save(existingDepartment);
        return convertToDTO(updatedDepartment);
    }

    @Override
    public void deleteDepartment(UUID id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban", "id", id));

        // Thay vì xóa, chỉ cần cập nhật trạng thái
        department.setStatus((byte) 0);
        department.setLastModifiedDate(Instant.now().toEpochMilli());
        departmentRepository.save(department);
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