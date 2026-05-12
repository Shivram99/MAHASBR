package com.mahasbr.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class NICCategoryEntity extends Auditable {

    @Id
    @Column(name = "category_code", nullable = false, unique = true)
    private String categoryCode;

    @Column(name = "description", nullable = false)
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY) 
    private Set<NICDivisionEntity> divisions;
    
    @Column(name = "is_active", nullable = false)
    private String isActive = "Y";

	public NICCategoryEntity(String categoryCode, String description, String isActive) {
		super();
		this.categoryCode = categoryCode;
		this.description = description;
		this.isActive = isActive;
	}
}
