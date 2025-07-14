package com.mahasbr.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nic_category")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NICCategoryEntity {

    @Id
    @Column(name = "category_code", nullable = false, unique = true)
    private String categoryCode;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference 
    private Set<NICDivisionEntity> divisions;
}
