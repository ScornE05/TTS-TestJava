package com.example.staffmanagement.service;

import com.example.staffmanagement.dto.ImportHistoryDTO;

import java.util.List;
import java.util.UUID;

public interface ImportHistoryService {
    List<ImportHistoryDTO> getAllImportHistories();
    ImportHistoryDTO getImportHistoryById(UUID id);
    ImportHistoryDTO createImportHistory(ImportHistoryDTO importHistoryDTO);
    void deleteImportHistory(UUID id);
}