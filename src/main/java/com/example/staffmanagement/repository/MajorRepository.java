package com.example.staffmanagement.repository;

import com.example.staffmanagement.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MajorRepository extends JpaRepository<Major, UUID> {
    Optional<Major> findByCode(String code);
    boolean existsByCode(String code);
}