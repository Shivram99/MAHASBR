package com.mahasbr.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
@Data
@Entity
@Table(name="menu_role_mapping",schema="public")
public class MstMenuRoleMapping {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mstMenuRoleMapping_seq_generator")
	@SequenceGenerator(name = "mstMenuRoleMapping_seq_generator", sequenceName = "mstMenuROleMapping_seq", allocationSize = 1)
		@Column(name="MENU_MAP_ID")
		private Long menuMapID;
		
		@Column(name="MENU_ID")
		private Long menuID;

		@Column(name="ROLE_ID")
		private Long roleId;
		
		@Column(name="is_active")
		private Character is_active;

		@Column(name = "CREATED_USER_ID")
		private Integer createdUserId;
		



}
