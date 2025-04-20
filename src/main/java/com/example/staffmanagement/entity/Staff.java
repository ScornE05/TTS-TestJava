package com.example.staffmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "staff")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Staff extends BaseEntity {

    @Column(name = "account_fe")
    private String accountFe;

    @Column(name = "account_fpt")
    private String accountFpt;

    @Column(name = "name")
    private String name;

    @Column(name = "staff_code")
    private String staffCode;

    @OneToMany(mappedBy = "staff")
    private Set<DepartmentFacility> departmentFacilities = new HashSet<>();

    @OneToMany(mappedBy = "staff")
    private Set<StaffMajorFacility> staffMajorFacilities = new HashSet<>();
}