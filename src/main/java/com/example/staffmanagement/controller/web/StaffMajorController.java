package com.example.staffmanagement.controller.web;

import com.example.staffmanagement.dto.StaffDTO;
import com.example.staffmanagement.dto.StaffMajorFacilityDTO;
import com.example.staffmanagement.dto.FacilityDTO;
import com.example.staffmanagement.dto.DepartmentDTO;
import com.example.staffmanagement.dto.MajorDTO;
import com.example.staffmanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/web/staff-majors")
public class StaffMajorController {

    private static final Logger logger = LoggerFactory.getLogger(StaffMajorController.class);

    private final StaffService staffService;
    private final FacilityService facilityService;
    private final DepartmentService departmentService;
    private final MajorService majorService;
    private final StaffMajorFacilityService staffMajorFacilityService;
    private final DepartmentFacilityService departmentFacilityService;
    private final MajorFacilityService majorFacilityService;

    @Autowired
    public StaffMajorController(StaffService staffService,
                                FacilityService facilityService,
                                DepartmentService departmentService,
                                MajorService majorService,
                                StaffMajorFacilityService staffMajorFacilityService,
                                DepartmentFacilityService departmentFacilityService,
                                MajorFacilityService majorFacilityService) {
        this.staffService = staffService;
        this.facilityService = facilityService;
        this.departmentService = departmentService;
        this.majorService = majorService;
        this.staffMajorFacilityService = staffMajorFacilityService;
        this.departmentFacilityService = departmentFacilityService;
        this.majorFacilityService = majorFacilityService;
    }

    @GetMapping("/staff/{staffId}")
    public String getStaffMajors(@PathVariable UUID staffId, Model model) {
        try {
            StaffDTO staff = staffService.getStaffById(staffId);
            List<StaffMajorFacilityDTO> staffMajors = staffMajorFacilityService.getStaffMajorFacilitiesByStaff(staffId);
            List<FacilityDTO> facilities = facilityService.getAllFacilities();

            model.addAttribute("staff", staff);
            model.addAttribute("staffMajors", staffMajors);
            model.addAttribute("facilities", facilities);
            model.addAttribute("title", "Quản lý bộ môn chuyên ngành - " + staff.getName());

            return "staff/majors";
        } catch (Exception e) {
            logger.error("Lỗi khi lấy thông tin bộ môn chuyên ngành của nhân viên: {}", e.getMessage());
            return "redirect:/web/staffs";
        }
    }

    @GetMapping("/departments/{facilityId}")
    @ResponseBody
    public ResponseEntity<List<DepartmentDTO>> getDepartmentsByFacility(@PathVariable UUID facilityId) {
        try {
            logger.debug("Lấy danh sách phòng ban theo cơ sở ID: {}", facilityId);

            // Lấy danh sách phòng ban theo cơ sở
            List<DepartmentDTO> departments = new ArrayList<>();

            var departmentFacilities = departmentFacilityService.getDepartmentFacilitiesByFacility(facilityId);
            if (departmentFacilities != null && !departmentFacilities.isEmpty()) {
                departments = departmentFacilities.stream()
                        .map(df -> {
                            try {
                                if (df == null || df.getDepartmentId() == null) {
                                    return null;
                                }

                                DepartmentDTO department = new DepartmentDTO();
                                department.setId(df.getDepartmentId());
                                department.setName(df.getDepartmentName() != null ? df.getDepartmentName() : "");

                                // Lấy code từ service để đảm bảo không null
                                try {
                                    var deptDetails = departmentService.getDepartmentById(df.getDepartmentId());
                                    if (deptDetails != null) {
                                        department.setCode(deptDetails.getCode());
                                    }
                                } catch (Exception e) {
                                    logger.warn("Không thể lấy chi tiết phòng ban: {}", e.getMessage());
                                    department.setCode("");
                                }

                                return department;
                            } catch (Exception e) {
                                logger.error("Lỗi khi xử lý phòng ban: {}", e.getMessage());
                                return null;
                            }
                        })
                        .filter(dept -> dept != null)
                        .collect(Collectors.toList());
            }

            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách phòng ban theo cơ sở: {}", e.getMessage());
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/majors/{facilityId}/{departmentId}")
    @ResponseBody
    public ResponseEntity<List<MajorDTO>> getMajorsByFacilityAndDepartment(
            @PathVariable UUID facilityId,
            @PathVariable UUID departmentId) {
        try {
            logger.debug("Lấy danh sách ngành học theo cơ sở ID: {} và phòng ban ID: {}", facilityId, departmentId);

            // Danh sách ngành học trả về
            List<MajorDTO> majors = new ArrayList<>();

            // Tìm departmentFacility theo facilityId và departmentId
            UUID departmentFacilityId = null;

            var departmentFacilities = departmentFacilityService.getAllDepartmentFacilities();
            if (departmentFacilities != null) {
                var matchingDF = departmentFacilities.stream()
                        .filter(df -> df.getFacilityId() != null && df.getDepartmentId() != null &&
                                df.getFacilityId().equals(facilityId) && df.getDepartmentId().equals(departmentId))
                        .findFirst();

                if (matchingDF.isPresent()) {
                    departmentFacilityId = matchingDF.get().getId();
                }
            }

            if (departmentFacilityId == null) {
                logger.warn("Không tìm thấy mối quan hệ giữa cơ sở {} và phòng ban {}", facilityId, departmentId);
                return ResponseEntity.ok(majors);
            }

            // Lấy danh sách ngành học theo cơ sở và phòng ban
            var majorFacilities = majorFacilityService.getMajorFacilitiesByDepartmentFacility(departmentFacilityId);
            if (majorFacilities != null && !majorFacilities.isEmpty()) {
                majors = majorFacilities.stream()
                        .map(mf -> {
                            try {
                                if (mf == null || mf.getMajorId() == null) {
                                    return null;
                                }

                                MajorDTO major = new MajorDTO();
                                major.setId(mf.getMajorId());
                                major.setName(mf.getMajorName() != null ? mf.getMajorName() : "");

                                // Lấy code từ service để đảm bảo không null
                                try {
                                    var majorDetails = majorService.getMajorById(mf.getMajorId());
                                    if (majorDetails != null) {
                                        major.setCode(majorDetails.getCode());
                                    }
                                } catch (Exception e) {
                                    logger.warn("Không thể lấy chi tiết ngành học: {}", e.getMessage());
                                    major.setCode("");
                                }

                                return major;
                            } catch (Exception e) {
                                logger.error("Lỗi khi xử lý ngành học: {}", e.getMessage());
                                return null;
                            }
                        })
                        .filter(major -> major != null)
                        .collect(Collectors.toList());
            }

            return ResponseEntity.ok(majors);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách ngành học: {}", e.getMessage());
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PostMapping("/add")
    public String addStaffMajor(
            @RequestParam UUID staffId,
            @RequestParam UUID facilityId,
            @RequestParam UUID departmentId,
            @RequestParam UUID majorId,
            RedirectAttributes redirectAttributes) {

        try {
            logger.debug("Thêm bộ môn chuyên ngành cho nhân viên ID: {}", staffId);

            // Kiểm tra xem nhân viên đã có chuyên ngành trong cơ sở này chưa
            boolean existsInFacility = false;
            List<StaffMajorFacilityDTO> staffMajorFacilities = staffMajorFacilityService.getStaffMajorFacilitiesByStaff(staffId);

            if (staffMajorFacilities != null && !staffMajorFacilities.isEmpty()) {
                for (StaffMajorFacilityDTO smf : staffMajorFacilities) {
                    try {
                        if (smf != null &&
                                smf.getMajorFacilityId() != null &&
                                majorFacilityService.getMajorFacilityById(smf.getMajorFacilityId()) != null &&
                                majorFacilityService.getMajorFacilityById(smf.getMajorFacilityId()).getDepartmentFacilityId() != null) {

                            var majorFacility = majorFacilityService.getMajorFacilityById(smf.getMajorFacilityId());
                            var departmentFacility = departmentFacilityService.getDepartmentFacilityById(
                                    majorFacility.getDepartmentFacilityId());

                            if (departmentFacility != null &&
                                    departmentFacility.getFacilityId() != null &&
                                    departmentFacility.getFacilityId().equals(facilityId)) {
                                existsInFacility = true;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Lỗi khi kiểm tra ngành học hiện tại: {}", e.getMessage());
                    }
                }
            }

            if (existsInFacility) {
                redirectAttributes.addFlashAttribute("error", "Nhân viên đã được phân công bộ môn chuyên ngành tại cơ sở này");
                return "redirect:/web/staff-majors/staff/" + staffId;
            }

            // Tìm departmentFacility
            UUID departmentFacilityId = null;
            List<com.example.staffmanagement.dto.DepartmentFacilityDTO> departmentFacilities =
                    departmentFacilityService.getAllDepartmentFacilities();

            if (departmentFacilities != null) {
                for (var df : departmentFacilities) {
                    if (df != null &&
                            df.getFacilityId() != null && df.getFacilityId().equals(facilityId) &&
                            df.getDepartmentId() != null && df.getDepartmentId().equals(departmentId)) {

                        departmentFacilityId = df.getId();
                        break;
                    }
                }
            }

            if (departmentFacilityId == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin liên kết giữa phòng ban và cơ sở");
                return "redirect:/web/staff-majors/staff/" + staffId;
            }

            // Tìm majorFacility
            UUID majorFacilityId = null;
            List<com.example.staffmanagement.dto.MajorFacilityDTO> majorFacilities =
                    majorFacilityService.getMajorFacilitiesByDepartmentFacility(departmentFacilityId);

            if (majorFacilities != null) {
                for (var mf : majorFacilities) {
                    if (mf != null &&
                            mf.getMajorId() != null && mf.getMajorId().equals(majorId)) {

                        majorFacilityId = mf.getId();
                        break;
                    }
                }
            }

            if (majorFacilityId == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin liên kết giữa ngành học, phòng ban và cơ sở");
                return "redirect:/web/staff-majors/staff/" + staffId;
            }

            // Tạo staffMajorFacility mới
            StaffMajorFacilityDTO staffMajorFacilityDTO = new StaffMajorFacilityDTO();
            staffMajorFacilityDTO.setId(UUID.randomUUID()); // Thêm ID mới
            staffMajorFacilityDTO.setStaffId(staffId);
            staffMajorFacilityDTO.setMajorFacilityId(majorFacilityId);
            staffMajorFacilityDTO.setStatus((byte) 1); // Set trạng thái hoạt động

            staffMajorFacilityService.createStaffMajorFacility(staffMajorFacilityDTO);

            redirectAttributes.addFlashAttribute("success", "Thêm bộ môn chuyên ngành cho nhân viên thành công");
        } catch (Exception e) {
            logger.error("Lỗi khi thêm bộ môn chuyên ngành: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/web/staff-majors/staff/" + staffId;
    }

    @GetMapping("/delete/{id}")
    public String deleteStaffMajor(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            logger.debug("Xóa bộ môn chuyên ngành ID: {}", id);

            StaffMajorFacilityDTO staffMajorFacility = staffMajorFacilityService.getStaffMajorFacilityById(id);
            if (staffMajorFacility == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin liên kết");
                return "redirect:/web/staffs";
            }

            UUID staffId = staffMajorFacility.getStaffId();

            staffMajorFacilityService.deleteStaffMajorFacility(id);

            redirectAttributes.addFlashAttribute("success", "Xóa bộ môn chuyên ngành thành công");
            return "redirect:/web/staff-majors/staff/" + staffId;
        } catch (Exception e) {
            logger.error("Lỗi khi xóa bộ môn chuyên ngành: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/staffs";
        }
    }
}