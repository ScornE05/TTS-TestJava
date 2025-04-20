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
        // Tạo một UUID mới nếu không được cung cấp
        if (staffDTO.getId() == null) {
            staffDTO.setId(UUID.randomUUID());
        }

        // Kiểm tra xem mã nhân viên đã tồn tại chưa
        if (isStaffCodeExists(staffDTO.getStaffCode())) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại: " + staffDTO.getStaffCode());
        }

        // Kiểm tra xem tài khoản FE đã tồn tại chưa
        if (staffDTO.getAccountFe() != null && isAccountFeExists(staffDTO.getAccountFe())) {
            throw new IllegalArgumentException("Tài khoản FE đã tồn tại: " + staffDTO.getAccountFe());
        }

        // Kiểm tra xem tài khoản FPT đã tồn tại chưa
        if (staffDTO.getAccountFpt() != null && isAccountFptExists(staffDTO.getAccountFpt())) {
            throw new IllegalArgumentException("Tài khoản FPT đã tồn tại: " + staffDTO.getAccountFpt());
        }

        // Kiểm tra các ràng buộc về định dạng email và độ dài
        validateStaffData(staffDTO);

        // Thiết lập trạng thái và thời gian
        staffDTO.setStatus((byte) 1);
        long currentTime = Instant.now().toEpochMilli();
        staffDTO.setCreatedDate(currentTime);
        staffDTO.setLastModifiedDate(currentTime);

        Staff staff = convertToEntity(staffDTO);
        staff = staffRepository.save(staff);
        return convertToDTO(staff);
    }

    // Phương thức để xác thực dữ liệu nhân viên
    private void validateStaffData(StaffDTO staffDTO) {
        // Kiểm tra độ dài của các trường
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

        // Kiểm tra xem mã nhân viên đã tồn tại chưa (nếu đã thay đổi)
        if (!existingStaff.getStaffCode().equals(staffDTO.getStaffCode())
                && isStaffCodeExists(staffDTO.getStaffCode())) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại: " + staffDTO.getStaffCode());
        }

        // Kiểm tra tài khoản FE (nếu đã thay đổi)
        if (staffDTO.getAccountFe() != null
                && !staffDTO.getAccountFe().equals(existingStaff.getAccountFe())
                && isAccountFeExists(staffDTO.getAccountFe())) {
            throw new IllegalArgumentException("Tài khoản FE đã tồn tại: " + staffDTO.getAccountFe());
        }

        // Kiểm tra tài khoản FPT (nếu đã thay đổi)
        if (staffDTO.getAccountFpt() != null
                && !staffDTO.getAccountFpt().equals(existingStaff.getAccountFpt())
                && isAccountFptExists(staffDTO.getAccountFpt())) {
            throw new IllegalArgumentException("Tài khoản FPT đã tồn tại: " + staffDTO.getAccountFpt());
        }

        // Cập nhật thông tin
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

        // 1. Đầu tiên, xóa tất cả StaffMajorFacility liên quan đến nhân viên này
        List<StaffMajorFacility> staffMajorFacilities = staffMajorFacilityRepository.findByStaff(staff);
        staffMajorFacilityRepository.deleteAll(staffMajorFacilities);

        // 2. Xóa các DepartmentFacility liên quan đến nhân viên này
        List<DepartmentFacility> departmentFacilities = departmentFacilityRepository.findByStaff(staff);

        for (DepartmentFacility df : departmentFacilities) {
            // 2.1 Trước khi xóa DepartmentFacility, cần xóa MajorFacility liên quan
            List<MajorFacility> majorFacilities = majorFacilityRepository.findByDepartmentFacility(df);

            for (MajorFacility mf : majorFacilities) {
                // 2.1.1 Xóa bất kỳ StaffMajorFacility nào liên quan đến MajorFacility này
                List<StaffMajorFacility> relatedSMFs = staffMajorFacilityRepository.findByMajorFacility(mf);
                staffMajorFacilityRepository.deleteAll(relatedSMFs);

                // 2.1.2 Xóa MajorFacility
                majorFacilityRepository.delete(mf);
            }

            // 2.2 Sau đó xóa DepartmentFacility
            departmentFacilityRepository.delete(df);
        }

        // 3. Cuối cùng xóa Staff
        staffRepository.delete(staff);
    }

    /**
     * Đổi trạng thái nhân viên (hoạt động/không hoạt động)
     */
    public StaffDTO toggleStaffStatus(UUID id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", id));

        // Đổi trạng thái từ 1 -> 0 hoặc từ 0 -> 1
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

    // Phương thức chuyển đổi từ Entity sang DTO
    private StaffDTO convertToDTO(Staff staff) {
        StaffDTO staffDTO = new StaffDTO();
        BeanUtils.copyProperties(staff, staffDTO);
        return staffDTO;
    }

    // Phương thức chuyển đổi từ DTO sang Entity
    private Staff convertToEntity(StaffDTO staffDTO) {
        Staff staff = new Staff();
        BeanUtils.copyProperties(staffDTO, staff);
        return staff;
    }
}