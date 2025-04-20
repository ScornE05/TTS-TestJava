package com.example.staffmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "import_history")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ImportHistory extends BaseEntity {

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "total_records")
    private Integer totalRecords;

    @Column(name = "success_records")
    private Integer successRecords;

    @Column(name = "failed_records")
    private Integer failedRecords;

    @Lob
    @Column(name = "success_details")
    private String successDetails;

    @Lob
    @Column(name = "failure_details")
    private String failureDetails;

    @Column(name = "imported_by")
    private String importedBy;
}