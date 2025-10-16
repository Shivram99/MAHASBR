package com.mahasbr.entity;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mst_registry_master")
public class RegistryMasterEntity extends Auditable{
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mst_registry_master_seq_gen")
    @SequenceGenerator(name = "mst_registry_master_seq_gen", sequenceName = "mst_registry_master_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "registry_name",  length = 200)
    private String registryName;
    
    @Column(name = "registry_name_en",  length = 200)
    private String registryNameEn;   // English name

    @Column(name = "registry_name_mr", length = 200)
    private String registryNameMr;   // Marathi name

    @Column(name = "status", length = 20)
    private Boolean status = true;

}
