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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/staff-majors")
public class StaffMajorController {

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
        StaffDTO staff = staffService.getStaffById(staffId);
        List<StaffMajorFacilityDTO> staffMajors = staffMajorFacilityService.getStaffMajorFacilitiesByStaff(staffId);
        List<FacilityDTO> facilities = facilityService.getAllFacilities();

        model.addAttribute("staff", staff);
        model.addAttribute("staffMajors", staffMajors);
        model.addAttribute("facilities", facilities);
        model.addAttribute("title", "Quản lý bộ môn chuyên ngành - " + staff.getName());

        return "staff/majors";
    }

    @GetMapping("/departments/{facilityId}")
    @ResponseBody
    public ResponseEntity<List<DepartmentDTO>> getDepartmentsByFacility(@PathVariable UUID facilityId) {
        // Lấy danh sách phòng ban theo cơ sở
        List<DepartmentDTO> departments = departmentFacilityService.getDepartmentFacilitiesByFacility(facilityId)
                .stream()
                .map(df -> {
                    DepartmentDTO department = new DepartmentDTO();
                    department.setId(df.getDepartmentId());
                    department.setName(df.getDepartmentName());
                    department.setCode(departmentService.getDepartmentById(df.getDepartmentId()).getCode());
                    return department;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(departments);
    }

    @GetMapping("/majors/{facilityId}/{departmentId}")
    @ResponseBody
    public ResponseEntity<List<MajorDTO>> getMajorsByFacilityAndDepartment(
            @PathVariable UUID facilityId,
            @PathVariable UUID departmentId) {

        // Tìm departmentFacility theo facilityId và departmentId
        UUID departmentFacilityId = departmentFacilityService
                .getAllDepartmentFacilities()
                .stream()
                .filter(df -> df.getFacilityId().equals(facilityId) && df.getDepartmentId().equals(departmentId))
                .findFirst()
                .map(df -> df.getId())
                .orElse(null);

        if (departmentFacilityId == null) {
            return ResponseEntity.ok(List.of());
        }

        // Lấy danh sách ngành học theo cơ sở và phòng ban
        List<MajorDTO> majors = majorFacilityService.getMajorFacilitiesByDepartmentFacility(departmentFacilityId)
                .stream()
                .map(mf -> {
                    MajorDTO major = new MajorDTO();
                    major.setId(mf.getMajorId());
                    major.setName(mf.getMajorName());
                    major.setCode(majorService.getMajorById(mf.getMajorId()).getCode());
                    return major;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(majors);
    }

    @PostMapping("/add")
    public String addStaffMajor(
            @RequestParam UUID staffId,
            @RequestParam UUID facilityId,
            @RequestParam UUID departmentId,
            @RequestParam UUID majorId,
            RedirectAttributes redirectAttributes) {

        try {
            // Kiểm tra xem nhân viên đã có chuyên ngành trong cơ sở này chưa
            boolean existsInFacility = staffMajorFacilityService.getStaffMajorFacilitiesByStaff(staffId)
                    .stream()
                    .anyMatch(smf -> smf.getMajorFacility().getDepartmentFacility().getFacility().getId().equals(facilityId));

            if (existsInFacility) {
                redirectAttributes.addFlashAttribute("error", "Nhân viên đã được phân công bộ môn chuyên ngành tại cơ sở này");
                return "redirect:/web/staff-majors/staff/" + staffId;
            }

            // Tìm departmentFacility
            UUID departmentFacilityId = departmentFacilityService
                    .getAllDepartmentFacilities()
                    .stream()
                    .filter(df -> df.getFacilityId().equals(facilityId) && df.getDepartmentId().equals(departmentId))
                    .findFirst()
                    .map(df -> df.getId())
                    .orElse(null);

            if (departmentFacilityId == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin liên kết giữa phòng ban và cơ sở");
                return "redirect:/web/staff-majors/staff/" + staffId;
            }

            // Tìm majorFacility
            UUID majorFacilityId = majorFacilityService
                    .getMajorFacilitiesByDepartmentFacility(departmentFacilityId)
                    .stream()
                    .filter(mf -> mf.getMajorId().equals(majorId))
                    .findFirst()
                    .map(mf -> mf.getId())
                    .orElse(null);

            if (majorFacilityId == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin liên kết giữa ngành học, phòng ban và cơ sở");
                return "redirect:/web/staff-majors/staff/" + staffId;
            }

            // Tạo staffMajorFacility mới
            StaffMajorFacilityDTO staffMajorFacilityDTO = new StaffMajorFacilityDTO();
            staffMajorFacilityDTO.setStaffId(staffId);
            staffMajorFacilityDTO.setMajorFacilityId(majorFacilityId);

            staffMajorFacilityService.createStaffMajorFacility(staffMajorFacilityDTO);

            redirectAttributes.addFlashAttribute("success", "Thêm bộ môn chuyên ngành cho nhân viên thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/web/staff-majors/staff/" + staffId;
    }

    @GetMapping("/delete/{id}")
    public String deleteStaffMajor(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            StaffMajorFacilityDTO staffMajorFacility = staffMajorFacilityService.getStaffMajorFacilityById(id);
            UUID staffId = staffMajorFacility.getStaffId();

            staffMajorFacilityService.deleteStaffMajorFacility(id);

            redirectAttributes.addFlashAttribute("success", "Xóa bộ môn chuyên ngành thành công");
            return "redirect:/web/staff-majors/staff/" + staffId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/staffs";
        }
    }
}