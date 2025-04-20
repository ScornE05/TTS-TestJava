package com.example.staffmanagement.repository;

import com.example.staffmanagement.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {
    Optional<Staff> findByStaffCode(String staffCode);
    Optional<Staff> findByAccountFe(String accountFe);
    Optional<Staff> findByAccountFpt(String accountFpt);
    boolean existsByStaffCode(String staffCode);
    boolean existsByAccountFe(String accountFe);
    boolean existsByAccountFpt(String accountFpt);
}