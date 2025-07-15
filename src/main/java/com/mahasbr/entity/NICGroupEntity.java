package com.mahasbr.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nic_group")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NICGroupEntity {

    @Id
    @Column(name = "group_code", nullable = false, unique = true)
    private String groupCode;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_code", nullable = false)
    @JsonBackReference // Child in the relationship with NICDivisionEntity
    private NICDivisionEntity division;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Parent in the relationship with NICClassEntity
    private Set<NICClassEntity> classes;
}
