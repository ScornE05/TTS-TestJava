package com.example.staffmanagement.service;

import com.example.staffmanagement.dto.MajorDTO;

import java.util.List;
import java.util.UUID;

public interface MajorService {
    List<MajorDTO> getAllMajors();
    MajorDTO getMajorById(UUID id);
    MajorDTO getMajorByCode(String code);
    MajorDTO createMajor(MajorDTO majorDTO);
    MajorDTO updateMajor(UUID id, MajorDTO majorDTO);
    void deleteMajor(UUID id);
    boolean isMajorCodeExists(String code);
}