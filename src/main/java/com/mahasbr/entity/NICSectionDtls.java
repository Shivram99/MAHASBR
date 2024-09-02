package com.mahasbr.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Entity
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "nic_section_dtls")
public class NICSectionDtls extends Auditable {

    @Id
    @Column(name = "section_code", unique = true, nullable = false)
    private String sectionCode;

    @Column(name = "section_name", nullable = false)
    private String sectionName;
    
}	
