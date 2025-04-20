package com.example.staffmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.CascadeType;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "department_facility")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentFacility extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "id_department")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "id_facility")
    private Facility facility;

    @ManyToOne
    @JoinColumn(name = "id_staff")
    private Staff staff;

    @OneToMany(mappedBy = "departmentFacility", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MajorFacility> majorFacilities = new HashSet<>();
}