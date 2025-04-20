package com.example.staffmanagement.service.impl;

import com.example.staffmanagement.dto.MajorFacilityDTO;
import com.example.staffmanagement.entity.DepartmentFacility;
import com.example.staffmanagement.entity.Major;
import com.example.staffmanagement.entity.MajorFacility;
import com.example.staffmanagement.exception.ResourceNotFoundException;
import com.example.staffmanagement.repository.DepartmentFacilityRepository;
import com.example.staffmanagement.repository.MajorFacilityRepository;
import com.example.staffmanagement.repository.MajorRepository;
import com.example.staffmanagement.service.MajorFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MajorFacilityServiceImpl implements MajorFacilityService {

    private final MajorFacilityRepository majorFacilityRepository;
    private final DepartmentFacilityRepository departmentFacilityRepository;
    private final MajorRepository majorRepository;

    @Autowired
    public MajorFacilityServiceImpl(
            MajorFacilityRepository majorFacilityRepository,
            DepartmentFacilityRepository departmentFacilityRepository,
            MajorRepository majorRepository) {
        this.majorFacilityRepository = majorFacilityRepository;
        this.departmentFacilityRepository = departmentFacilityRepository;
        this.majorRepository = majorRepository;
    }

    @Override
    public List<MajorFacilityDTO> getAllMajorFacilities() {
        List<MajorFacility> majorFacilities = majorFacilityRepository.findAll();
        return majorFacilities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MajorFacilityDTO getMajorFacilityById(UUID id) {
        MajorFacility majorFacility = majorFacilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ngành học - Cơ sở", "id", id));
        return convertToDTO(majorFacility);
    }

    @Override
    public List<MajorFacilityDTO> getMajorFacilitiesByDepartmentFacility(UUID departmentFacilityId) {
        DepartmentFacility departmentFacility = departmentFacilityRepository.findById(departmentFacilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban - Cơ sở", "id", departmentFacilityId));

        List<MajorFacility> majorFacilities = majorFacilityRepository.findByDepartmentFacility(departmentFacility);
        return majorFacilities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MajorFacilityDTO> getMajorFacilitiesByMajor(UUID majorId) {
        Major major = majorRepository.findById(majorId)
                .orElseThrow(() -> new ResourceNotFoundException("Ngành học", "id", majorId));

        List<MajorFacility> majorFacilities = majorFacilityRepository.findByMajor(major);
        return majorFacilities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MajorFacilityDTO createMajorFacility(MajorFacilityDTO majorFacilityDTO) {
        // Tạo một UUID mới nếu không được cung cấp
        if (majorFacilityDTO.getId() == null) {
            majorFacilityDTO.setId(UUID.randomUUID());
        }

        // Lấy các thực thể tham chiếu
        DepartmentFacility departmentFacility = departmentFacilityRepository.findById(majorFacilityDTO.getDepartmentFacilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban - Cơ sở", "id", majorFacilityDTO.getDepartmentFacilityId()));

        Major major = majorRepository.findById(majorFacilityDTO.getMajorId())
                .orElseThrow(() -> new ResourceNotFoundException("Ngành học", "id", majorFacilityDTO.getMajorId()));

        // Kiểm tra xem cặp ngành học-cơ sở đã tồn tại chưa
        if (existsByDepartmentFacilityAndMajor(departmentFacility.getId(), major.getId())) {
            throw new IllegalArgumentException("Cặp ngành học-cơ sở đã tồn tại");
        }

        // Thiết lập trạng thái và thời gian
        majorFacilityDTO.setStatus((byte) 1);
        long currentTime = Instant.now().toEpochMilli();
        majorFacilityDTO.setCreatedDate(currentTime);
        majorFacilityDTO.setLastModifiedDate(currentTime);

        // Tạo mới thực thể
        MajorFacility majorFacility = new MajorFacility();
        majorFacility.setId(majorFacilityDTO.getId());
        majorFacility.setStatus(majorFacilityDTO.getStatus());
        majorFacility.setCreatedDate(majorFacilityDTO.getCreatedDate());
        majorFacility.setLastModifiedDate(majorFacilityDTO.getLastModifiedDate());
        majorFacility.setDepartmentFacility(departmentFacility);
        majorFacility.setMajor(major);

        majorFacility = majorFacilityRepository.save(majorFacility);
        return convertToDTO(majorFacility);
    }

    @Override
    public MajorFacilityDTO updateMajorFacility(UUID id, MajorFacilityDTO majorFacilityDTO) {
        MajorFacility existingMajorFacility = majorFacilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ngành học - Cơ sở", "id", id));

        // Lấy các thực thể tham chiếu mới
        DepartmentFacility departmentFacility = departmentFacilityRepository.findById(majorFacilityDTO.getDepartmentFacilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban - Cơ sở", "id", majorFacilityDTO.getDepartmentFacilityId()));

        Major major = majorRepository.findById(majorFacilityDTO.getMajorId())
                .orElseThrow(() -> new ResourceNotFoundException("Ngành học", "id", majorFacilityDTO.getMajorId()));

        // Kiểm tra xem cặp ngành học-cơ sở đã tồn tại chưa (nếu đã thay đổi)
        boolean isDepartmentFacilityChanged = !existingMajorFacility.getDepartmentFacility().getId().equals(departmentFacility.getId());
        boolean isMajorChanged = !existingMajorFacility.getMajor().getId().equals(major.getId());

        if ((isDepartmentFacilityChanged || isMajorChanged)
                && existsByDepartmentFacilityAndMajor(departmentFacility.getId(), major.getId())) {
            throw new IllegalArgumentException("Cặp ngành học-cơ sở đã tồn tại");
        }

        // Cập nhật thông tin
        existingMajorFacility.setDepartmentFacility(departmentFacility);
        existingMajorFacility.setMajor(major);
        existingMajorFacility.setLastModifiedDate(Instant.now().toEpochMilli());

        MajorFacility updatedMajorFacility = majorFacilityRepository.save(existingMajorFacility);
        return convertToDTO(updatedMajorFacility);
    }

    @Override
    public void deleteMajorFacility(UUID id) {
        MajorFacility majorFacility = majorFacilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ngành học - Cơ sở", "id", id));

        // Thay vì xóa, chỉ cần cập nhật trạng thái
        majorFacility.setStatus((byte) 0);
        majorFacility.setLastModifiedDate(Instant.now().toEpochMilli());
        majorFacilityRepository.save(majorFacility);
    }

    @Override
    public boolean existsByDepartmentFacilityAndMajor(UUID departmentFacilityId, UUID majorId) {
        DepartmentFacility departmentFacility = departmentFacilityRepository.findById(departmentFacilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban - Cơ sở", "id", departmentFacilityId));

        Major major = majorRepository.findById(majorId)
                .orElseThrow(() -> new ResourceNotFoundException("Ngành học", "id", majorId));

        return majorFacilityRepository.existsByDepartmentFacilityAndMajor(departmentFacility, major);
    }

    // Phương thức chuyển đổi từ Entity sang DTO
    private MajorFacilityDTO convertToDTO(MajorFacility majorFacility) {
        MajorFacilityDTO dto = new MajorFacilityDTO();
        dto.setId(majorFacility.getId());
        dto.setStatus(majorFacility.getStatus());
        dto.setCreatedDate(majorFacility.getCreatedDate());
        dto.setLastModifiedDate(majorFacility.getLastModifiedDate());

        dto.setDepartmentFacilityId(majorFacility.getDepartmentFacility().getId());
        dto.setMajorId(majorFacility.getMajor().getId());

        dto.setDepartmentName(majorFacility.getDepartmentFacility().getDepartment().getName());
        dto.setFacilityName(majorFacility.getDepartmentFacility().getFacility().getName());
        dto.setMajorName(majorFacility.getMajor().getName());

        return dto;
    }
}