package com.mahasbr.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Entity
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "nic_division_dtls")
public class NICDivisionDtls {

	    @Id
	    @Column(name = "division_code", unique = true, nullable = false)
	    private String divisionCode;

	    @Column(name = "division_name", nullable = false)
	    private String divisionName;
	
//	    @ManyToOne
//	    @JoinColumn(name = "section_code", referencedColumnName = "section_code")
//	    private NICSectionDtls nicSectionDtls;
}
