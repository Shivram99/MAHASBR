package com.mahasbr.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "MstMenu")
@NoArgsConstructor
public class MstMenu {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mstMenu_seq_generator")
	@SequenceGenerator(name = "mstMenu_seq_generator", sequenceName = "mstMenu_seq", allocationSize = 1)
	@Column(name = "MENU_ID")
	private Long menuId;

	@Column(name = "MENU_CODE")
	private Long menuCode;

	@Column(name = "menu_name_english")
	private String menu_name_english;

	@Column(name = "menu_name_marathi")
	private String menu_name_marathi;

	@Column(name = "is_active")
	private String is_active;

	@Column(name = "CREATED_USER_ID")
	private Integer createdUserId;

	@Column(name = "UPDATED_USER_ID")
	private Integer updatedUserId;

}
