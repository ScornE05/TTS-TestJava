package com.example.staffmanagement.service.impl;

import com.example.staffmanagement.dto.ImportHistoryDTO;
import com.example.staffmanagement.entity.ImportHistory;
import com.example.staffmanagement.exception.ResourceNotFoundException;
import com.example.staffmanagement.repository.ImportHistoryRepository;
import com.example.staffmanagement.service.ImportHistoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ImportHistoryServiceImpl implements ImportHistoryService {

    private final ImportHistoryRepository importHistoryRepository;

    @Autowired
    public ImportHistoryServiceImpl(ImportHistoryRepository importHistoryRepository) {
        this.importHistoryRepository = importHistoryRepository;
    }

    @Override
    public List<ImportHistoryDTO> getAllImportHistories() {
        List<ImportHistory> histories = importHistoryRepository.findAllByOrderByCreatedDateDesc();
        return histories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ImportHistoryDTO getImportHistoryById(UUID id) {
        ImportHistory history = importHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lịch sử import", "id", id));
        return convertToDTO(history);
    }

    @Override
    public ImportHistoryDTO createImportHistory(ImportHistoryDTO importHistoryDTO) {

        if (importHistoryDTO.getId() == null) {
            importHistoryDTO.setId(UUID.randomUUID());
        }

        importHistoryDTO.setStatus((byte) 1);
        long currentTime = Instant.now().toEpochMilli();
        importHistoryDTO.setCreatedDate(currentTime);
        importHistoryDTO.setLastModifiedDate(currentTime);

        ImportHistory importHistory = convertToEntity(importHistoryDTO);
        importHistory = importHistoryRepository.save(importHistory);
        return convertToDTO(importHistory);
    }

    @Override
    public void deleteImportHistory(UUID id) {
        ImportHistory history = importHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lịch sử import", "id", id));

        importHistoryRepository.delete(history);
    }

    private ImportHistoryDTO convertToDTO(ImportHistory importHistory) {
        ImportHistoryDTO importHistoryDTO = new ImportHistoryDTO();
        BeanUtils.copyProperties(importHistory, importHistoryDTO);
        return importHistoryDTO;
    }

    private ImportHistory convertToEntity(ImportHistoryDTO importHistoryDTO) {
        ImportHistory importHistory = new ImportHistory();
        BeanUtils.copyProperties(importHistoryDTO, importHistory);
        return importHistory;
    }
}