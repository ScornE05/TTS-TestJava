package com.example.staffmanagement.repository;

import com.example.staffmanagement.entity.ImportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImportHistoryRepository extends JpaRepository<ImportHistory, UUID> {
    List<ImportHistory> findAllByOrderByCreatedDateDesc();
}