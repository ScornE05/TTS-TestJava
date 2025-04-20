package com.example.staffmanagement.repository;

import com.example.staffmanagement.entity.DepartmentFacility;
import com.example.staffmanagement.entity.Major;
import com.example.staffmanagement.entity.MajorFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MajorFacilityRepository extends JpaRepository<MajorFacility, UUID> {
    List<MajorFacility> findByDepartmentFacility(DepartmentFacility departmentFacility);
    List<MajorFacility> findByMajor(Major major);
    Optional<MajorFacility> findByDepartmentFacilityAndMajor(DepartmentFacility departmentFacility, Major major);
    boolean existsByDepartmentFacilityAndMajor(DepartmentFacility departmentFacility, Major major);
}