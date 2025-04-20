package com.example.staffmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "major_facility")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MajorFacility extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "id_department_facility")
    private DepartmentFacility departmentFacility;

    @ManyToOne
    @JoinColumn(name = "id_major")
    private Major major;

    @OneToMany(mappedBy = "majorFacility")
    private Set<StaffMajorFacility> staffMajorFacilities = new HashSet<>();
}