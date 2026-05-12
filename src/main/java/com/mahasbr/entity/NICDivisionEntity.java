package com.mahasbr.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nic_division")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NICDivisionEntity extends Auditable{

    @Id
    @Column(name = "division_code", nullable = false, unique = true)
    private String divisionCode;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore // Child in the relationship with NICCategoryEntity
    private NICCategoryEntity category;

    @OneToMany(mappedBy = "division", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Parent in the relationship with NICGroupEntity
    private Set<NICGroupEntity> groups;
    
    @Column(name = "is_active", nullable = false)
    private String isActive = "Y";
}