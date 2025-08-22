package com.mahasbr.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="menu_role_mapping")
public class MstMenuRoleMappingEntity extends Auditable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="MENU_MAP_ID")
	private Integer menuMapID;
	
	@Column(name="MENU_CODE")
	private Integer menuCode;

	@Column(name="ROLE_ID")
	private Integer roleId;
	
	@Column(name="is_active")
	private Character is_active;
	
}