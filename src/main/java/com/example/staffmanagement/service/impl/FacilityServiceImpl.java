package com.example.staffmanagement.service.impl;

import com.example.staffmanagement.dto.FacilityDTO;
import com.example.staffmanagement.entity.Facility;
import com.example.staffmanagement.exception.ResourceNotFoundException;
import com.example.staffmanagement.repository.FacilityRepository;
import com.example.staffmanagement.service.FacilityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;

    @Autowired
    public FacilityServiceImpl(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @Override
    public List<FacilityDTO> getAllFacilities() {
        List<Facility> facilities = facilityRepository.findAll();
        return facilities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FacilityDTO getFacilityById(UUID id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cơ sở", "id", id));
        return convertToDTO(facility);
    }

    @Override
    public FacilityDTO getFacilityByCode(String code) {
        Facility facility = facilityRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Cơ sở", "mã", code));
        return convertToDTO(facility);
    }

    @Override
    public FacilityDTO createFacility(FacilityDTO facilityDTO) {
        // Tạo một UUID mới nếu không được cung cấp
        if (facilityDTO.getId() == null) {
            facilityDTO.setId(UUID.randomUUID());
        }

        // Kiểm tra xem mã cơ sở đã tồn tại chưa
        if (isFacilityCodeExists(facilityDTO.getCode())) {
            throw new IllegalArgumentException("Mã cơ sở đã tồn tại: " + facilityDTO.getCode());
        }

        // Thiết lập trạng thái và thời gian
        facilityDTO.setStatus((byte) 1);
        long currentTime = Instant.now().toEpochMilli();
        facilityDTO.setCreatedDate(currentTime);
        facilityDTO.setLastModifiedDate(currentTime);

        Facility facility = convertToEntity(facilityDTO);
        facility = facilityRepository.save(facility);
        return convertToDTO(facility);
    }

    @Override
    public FacilityDTO updateFacility(UUID id, FacilityDTO facilityDTO) {
        Facility existingFacility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cơ sở", "id", id));

        // Kiểm tra xem mã cơ sở đã tồn tại chưa (nếu đã thay đổi)
        if (!existingFacility.getCode().equals(facilityDTO.getCode())
                && isFacilityCodeExists(facilityDTO.getCode())) {
            throw new IllegalArgumentException("Mã cơ sở đã tồn tại: " + facilityDTO.getCode());
        }

        // Cập nhật thông tin
        existingFacility.setName(facilityDTO.getName());
        existingFacility.setCode(facilityDTO.getCode());
        existingFacility.setLastModifiedDate(Instant.now().toEpochMilli());

        Facility updatedFacility = facilityRepository.save(existingFacility);
        return convertToDTO(updatedFacility);
    }

    @Override
    public void deleteFacility(UUID id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cơ sở", "id", id));

        // Xóa thực sự thay vì cập nhật trạng thái
        facilityRepository.delete(facility);
    }

    @Override
    public boolean isFacilityCodeExists(String code) {
        return facilityRepository.existsByCode(code);
    }

    // Phương thức chuyển đổi từ Entity sang DTO
    private FacilityDTO convertToDTO(Facility facility) {
        FacilityDTO facilityDTO = new FacilityDTO();
        BeanUtils.copyProperties(facility, facilityDTO);
        return facilityDTO;
    }

    // Phương thức chuyển đổi từ DTO sang Entity
    private Facility convertToEntity(FacilityDTO facilityDTO) {
        Facility facility = new Facility();
        BeanUtils.copyProperties(facilityDTO, facility);
        return facility;
    }
}