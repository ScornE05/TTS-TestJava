package com.example.staffmanagement.repository;

import com.example.staffmanagement.entity.Department;
import com.example.staffmanagement.entity.DepartmentFacility;
import com.example.staffmanagement.entity.Facility;
import com.example.staffmanagement.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentFacilityRepository extends JpaRepository<DepartmentFacility, UUID> {
    List<DepartmentFacility> findByDepartment(Department department);
    List<DepartmentFacility> findByFacility(Facility facility);
    List<DepartmentFacility> findByStaff(Staff staff);
    Optional<DepartmentFacility> findByDepartmentAndFacility(Department department, Facility facility);
    boolean existsByDepartmentAndFacility(Department department, Facility facility);
}