package com.example.staffmanagement.service.impl;

import com.example.staffmanagement.dto.StaffDTO;
import com.example.staffmanagement.entity.DepartmentFacility;
import com.example.staffmanagement.entity.MajorFacility;
import com.example.staffmanagement.entity.Staff;
import com.example.staffmanagement.entity.StaffMajorFacility;
import com.example.staffmanagement.exception.ResourceNotFoundException;
import com.example.staffmanagement.repository.DepartmentFacilityRepository;
import com.example.staffmanagement.repository.MajorFacilityRepository;
import com.example.staffmanagement.repository.StaffMajorFacilityRepository;
import com.example.staffmanagement.repository.StaffRepository;
import com.example.staffmanagement.service.StaffService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final StaffMajorFacilityRepository staffMajorFacilityRepository;
    private final DepartmentFacilityRepository departmentFacilityRepository;
    private final MajorFacilityRepository majorFacilityRepository;

    @Autowired
    public StaffServiceImpl(
            StaffRepository staffRepository,
            StaffMajorFacilityRepository staffMajorFacilityRepository,
            DepartmentFacilityRepository departmentFacilityRepository,
            MajorFacilityRepository majorFacilityRepository) {
        this.staffRepository = staffRepository;
        this.staffMajorFacilityRepository = staffMajorFacilityRepository;
        this.departmentFacilityRepository = departmentFacilityRepository;
        this.majorFacilityRepository = majorFacilityRepository;
    }

    @Override
    public List<StaffDTO> getAllStaffs() {
        List<Staff> staffs = staffRepository.findAll();
        return staffs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StaffDTO getStaffById(UUID id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", id));
        return convertToDTO(staff);
    }

    @Override
    public StaffDTO getStaffByStaffCode(String staffCode) {
        Staff staff = staffRepository.findByStaffCode(staffCode)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "mã nhân viên", staffCode));
        return convertToDTO(staff);
    }

    @Override
    public StaffDTO createStaff(StaffDTO staffDTO) {

        if (staffDTO.getId() == null) {
            staffDTO.setId(UUID.randomUUID());
        }

        if (isStaffCodeExists(staffDTO.getStaffCode())) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại: " + staffDTO.getStaffCode());
        }

        if (staffDTO.getAccountFe() != null && isAccountFeExists(staffDTO.getAccountFe())) {
            throw new IllegalArgumentException("Tài khoản FE đã tồn tại: " + staffDTO.getAccountFe());
        }

        if (staffDTO.getAccountFpt() != null && isAccountFptExists(staffDTO.getAccountFpt())) {
            throw new IllegalArgumentException("Tài khoản FPT đã tồn tại: " + staffDTO.getAccountFpt());
        }

        validateStaffData(staffDTO);

        staffDTO.setStatus((byte) 1);
        long currentTime = Instant.now().toEpochMilli();
        staffDTO.setCreatedDate(currentTime);
        staffDTO.setLastModifiedDate(currentTime);

        Staff staff = convertToEntity(staffDTO);
        staff = staffRepository.save(staff);
        return convertToDTO(staff);
    }

    private void validateStaffData(StaffDTO staffDTO) {

        if (staffDTO.getName() != null && staffDTO.getName().length() > 100) {
            throw new IllegalArgumentException("Tên nhân viên không được vượt quá 100 ký tự");
        }

        if (staffDTO.getStaffCode() != null && staffDTO.getStaffCode().length() > 15) {
            throw new IllegalArgumentException("Mã nhân viên không được vượt quá 15 ký tự");
        }

        if (staffDTO.getAccountFe() != null) {
            if (staffDTO.getAccountFe().length() > 100) {
                throw new IllegalArgumentException("Email FE không được vượt quá 100 ký tự");
            }

            if (!staffDTO.getAccountFe().endsWith("@fe.edu.vn")) {
                throw new IllegalArgumentException("Email FE phải có định dạng @fe.edu.vn");
            }

            if (staffDTO.getAccountFe().contains(" ")) {
                throw new IllegalArgumentException("Email FE không được chứa khoảng trắng");
            }
        }

        if (staffDTO.getAccountFpt() != null) {
            if (staffDTO.getAccountFpt().length() > 100) {
                throw new IllegalArgumentException("Email FPT không được vượt quá 100 ký tự");
            }

            if (!staffDTO.getAccountFpt().endsWith("@fpt.edu.vn")) {
                throw new IllegalArgumentException("Email FPT phải có định dạng @fpt.edu.vn");
            }

            if (staffDTO.getAccountFpt().contains(" ")) {
                throw new IllegalArgumentException("Email FPT không được chứa khoảng trắng");
            }
        }
    }

    @Override
    public StaffDTO updateStaff(UUID id, StaffDTO staffDTO) {
        Staff existingStaff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", id));

        if (!existingStaff.getStaffCode().equals(staffDTO.getStaffCode())
                && isStaffCodeExists(staffDTO.getStaffCode())) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại: " + staffDTO.getStaffCode());
        }

        if (staffDTO.getAccountFe() != null
                && !staffDTO.getAccountFe().equals(existingStaff.getAccountFe())
                && isAccountFeExists(staffDTO.getAccountFe())) {
            throw new IllegalArgumentException("Tài khoản FE đã tồn tại: " + staffDTO.getAccountFe());
        }
        if (staffDTO.getAccountFpt() != null
                && !staffDTO.getAccountFpt().equals(existingStaff.getAccountFpt())
                && isAccountFptExists(staffDTO.getAccountFpt())) {
            throw new IllegalArgumentException("Tài khoản FPT đã tồn tại: " + staffDTO.getAccountFpt());
        }
        existingStaff.setName(staffDTO.getName());
        existingStaff.setStaffCode(staffDTO.getStaffCode());
        existingStaff.setAccountFe(staffDTO.getAccountFe());
        existingStaff.setAccountFpt(staffDTO.getAccountFpt());
        existingStaff.setLastModifiedDate(Instant.now().toEpochMilli());

        Staff updatedStaff = staffRepository.save(existingStaff);
        return convertToDTO(updatedStaff);
    }

    @Override
    @Transactional
    public void deleteStaff(UUID id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", id));
        List<StaffMajorFacility> staffMajorFacilities = staffMajorFacilityRepository.findByStaff(staff);
        staffMajorFacilityRepository.deleteAll(staffMajorFacilities);
        List<DepartmentFacility> departmentFacilities = departmentFacilityRepository.findByStaff(staff);

        for (DepartmentFacility df : departmentFacilities) {
            List<MajorFacility> majorFacilities = majorFacilityRepository.findByDepartmentFacility(df);

            for (MajorFacility mf : majorFacilities) {
                List<StaffMajorFacility> relatedSMFs = staffMajorFacilityRepository.findByMajorFacility(mf);
                staffMajorFacilityRepository.deleteAll(relatedSMFs);
                majorFacilityRepository.delete(mf);
            }

            departmentFacilityRepository.delete(df);
        }

        staffRepository.delete(staff);
    }

    public StaffDTO toggleStaffStatus(UUID id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", id));

        staff.setStatus(staff.getStatus() == 1 ? (byte) 0 : (byte) 1);
        staff.setLastModifiedDate(Instant.now().toEpochMilli());

        staff = staffRepository.save(staff);
        return convertToDTO(staff);
    }

    @Override
    public boolean isStaffCodeExists(String staffCode) {
        return staffRepository.existsByStaffCode(staffCode);
    }

    @Override
    public boolean isAccountFeExists(String accountFe) {
        return staffRepository.existsByAccountFe(accountFe);
    }

    @Override
    public boolean isAccountFptExists(String accountFpt) {
        return staffRepository.existsByAccountFpt(accountFpt);
    }

    private StaffDTO convertToDTO(Staff staff) {
        StaffDTO staffDTO = new StaffDTO();
        BeanUtils.copyProperties(staff, staffDTO);
        return staffDTO;
    }

    private Staff convertToEntity(StaffDTO staffDTO) {
        Staff staff = new Staff();
        BeanUtils.copyProperties(staffDTO, staff);
        return staff;
    }
}