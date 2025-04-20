package com.example.staffmanagement.repository;

import com.example.staffmanagement.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, UUID> {
    Optional<Facility> findByCode(String code);
    boolean existsByCode(String code);
}