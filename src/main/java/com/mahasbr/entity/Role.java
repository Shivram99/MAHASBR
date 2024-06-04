
package com.mahasbr.entity;

import com.mahasbr.model.ERole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "roles")
public class Role extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq_generator")
    @SequenceGenerator(name = "role_seq_generator", sequenceName = "roles_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

	/*
	 * @ManyToMany(mappedBy = "role", cascade = CascadeType.ALL)
	 * Set<MstMenuRoleMapping> mstMenuRoleMapping;
	 * 
	 * @ManyToMany(mappedBy = "role", cascade = CascadeType.ALL) Set<MstSubMenu>
	 * mstSubMenu;
	 */
}

