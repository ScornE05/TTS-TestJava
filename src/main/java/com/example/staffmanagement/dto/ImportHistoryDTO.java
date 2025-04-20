package com.example.staffmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ImportHistoryDTO extends BaseDTO {

    private String fileName;
    private Integer totalRecords;
    private Integer successRecords;
    private Integer failedRecords;
    private String successDetails;
    private String failureDetails;
    private String importedBy;
}