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
@Table(name = "MENU_MST")
public class MstMenuEntity extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MENU_ID")
	private Integer menuId;

	@Column(name = "MENU_CODE")
	private Integer menuCode;

	@Column(name = "menu_name_english")
	private String menu_name_english;

	@Column(name = "menu_name_marathi")
	private String menu_name_marathi;

	@Column(name = "is_active")
	private String is_active;

	@Column(name = "icon")
	private String icon;

}