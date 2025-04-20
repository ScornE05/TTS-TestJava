package com.example.staffmanagement.repository;

import com.example.staffmanagement.entity.MajorFacility;
import com.example.staffmanagement.entity.Staff;
import com.example.staffmanagement.entity.StaffMajorFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffMajorFacilityRepository extends JpaRepository<StaffMajorFacility, UUID> {
    List<StaffMajorFacility> findByStaff(Staff staff);
    List<StaffMajorFacility> findByMajorFacility(MajorFacility majorFacility);
    Optional<StaffMajorFacility> findByStaffAndMajorFacility(Staff staff, MajorFacility majorFacility);
    boolean existsByStaffAndMajorFacility(Staff staff, MajorFacility majorFacility);
}