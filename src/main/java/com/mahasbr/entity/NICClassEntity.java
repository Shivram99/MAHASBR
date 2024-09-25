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
@Table(name = "nic_class")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NICClassEntity {

    @Id
    @Column(name = "class_code", nullable = false, unique = true)
    private String classCode;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_code", nullable = false)
    @JsonBackReference // Child in the relationship with NICGroupEntity
    private NICGroupEntity group;

    @OneToMany(mappedBy = "nicClass", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Parent in the relationship with NICCodeEntity
    private Set<NICCodeEntity> nicCodes;
}
