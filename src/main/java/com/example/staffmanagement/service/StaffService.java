package com.example.staffmanagement.service;

import com.example.staffmanagement.dto.StaffDTO;

import java.util.List;
import java.util.UUID;

public interface StaffService {
    List<StaffDTO> getAllStaffs();
    StaffDTO getStaffById(UUID id);
    StaffDTO getStaffByStaffCode(String staffCode);
    StaffDTO createStaff(StaffDTO staffDTO);
    StaffDTO updateStaff(UUID id, StaffDTO staffDTO);
    void deleteStaff(UUID id);
    StaffDTO toggleStaffStatus(UUID id);
    boolean isStaffCodeExists(String staffCode);
    boolean isAccountFeExists(String accountFe);
    boolean isAccountFptExists(String accountFpt);
}