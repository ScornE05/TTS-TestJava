package com.example.staffmanagement.service.impl;

import com.example.staffmanagement.dto.MajorDTO;
import com.example.staffmanagement.entity.Major;
import com.example.staffmanagement.exception.ResourceNotFoundException;
import com.example.staffmanagement.repository.MajorRepository;
import com.example.staffmanagement.service.MajorService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MajorServiceImpl implements MajorService {

    private final MajorRepository majorRepository;

    @Autowired
    public MajorServiceImpl(MajorRepository majorRepository) {
        this.majorRepository = majorRepository;
    }

    @Override
    public List<MajorDTO> getAllMajors() {
        List<Major> majors = majorRepository.findAll();
        return majors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MajorDTO getMajorById(UUID id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ngành học", "id", id));
        return convertToDTO(major);
    }

    @Override
    public MajorDTO getMajorByCode(String code) {
        Major major = majorRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Ngành học", "mã", code));
        return convertToDTO(major);
    }

    @Override
    public MajorDTO createMajor(MajorDTO majorDTO) {
        // Tạo một UUID mới nếu không được cung cấp
        if (majorDTO.getId() == null) {
            majorDTO.setId(UUID.randomUUID());
        }

        // Kiểm tra xem mã ngành học đã tồn tại chưa
        if (isMajorCodeExists(majorDTO.getCode())) {
            throw new IllegalArgumentException("Mã ngành học đã tồn tại: " + majorDTO.getCode());
        }

        // Thiết lập trạng thái và thời gian
        majorDTO.setStatus((byte) 1);
        long currentTime = Instant.now().toEpochMilli();
        majorDTO.setCreatedDate(currentTime);
        majorDTO.setLastModifiedDate(currentTime);

        Major major = convertToEntity(majorDTO);
        major = majorRepository.save(major);
        return convertToDTO(major);
    }

    @Override
    public MajorDTO updateMajor(UUID id, MajorDTO majorDTO) {
        Major existingMajor = majorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ngành học", "id", id));

        // Kiểm tra xem mã ngành học đã tồn tại chưa (nếu đã thay đổi)
        if (!existingMajor.getCode().equals(majorDTO.getCode())
                && isMajorCodeExists(majorDTO.getCode())) {
            throw new IllegalArgumentException("Mã ngành học đã tồn tại: " + majorDTO.getCode());
        }

        // Cập nhật thông tin
        existingMajor.setName(majorDTO.getName());
        existingMajor.setCode(majorDTO.getCode());
        existingMajor.setLastModifiedDate(Instant.now().toEpochMilli());

        Major updatedMajor = majorRepository.save(existingMajor);
        return convertToDTO(updatedMajor);
    }

    @Override
    public void deleteMajor(UUID id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ngành học", "id", id));

        majorRepository.delete(major);
    }

    @Override
    public boolean isMajorCodeExists(String code) {
        return majorRepository.existsByCode(code);
    }

    // Phương thức chuyển đổi từ Entity sang DTO
    private MajorDTO convertToDTO(Major major) {
        MajorDTO majorDTO = new MajorDTO();
        BeanUtils.copyProperties(major, majorDTO);
        return majorDTO;
    }

    // Phương thức chuyển đổi từ DTO sang Entity
    private Major convertToEntity(MajorDTO majorDTO) {
        Major major = new Major();
        BeanUtils.copyProperties(majorDTO, major);
        return major;
    }
}