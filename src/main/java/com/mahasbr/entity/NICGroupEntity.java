package com.mahasbr.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NICGroupEntity extends Auditable{

    @Id
    @Column(name = "group_code", nullable = false, unique = true)
    private String groupCode;

    @Column(name = "description", nullable = false)
    private String description;

    // Many groups belong to one division
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_code", nullable = false)
    @JsonIgnoreProperties({"groups"}) 
    private NICDivisionEntity division;

    // One group can have many classes
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"group"})
    private Set<NICClassEntity> classes;

    @Column(name = "is_active", nullable = false)
    private String isActive = "Y";
}
