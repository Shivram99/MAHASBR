package com.mahasbr.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "MstMenu")
@NoArgsConstructor
public class MstMenu extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mstmenu_seq_generator")
	@SequenceGenerator(name = "mstmenu_seq_generator", sequenceName = "mstmenu_seq", allocationSize = 1)
	@Column(name = "menu_id")
	private Long menuId;

	@Column(name = "menu_name_english")
	private String menuNameEnglish;

	@Column(name = "menu_name_marathi")
	private String menuNameMarathi;

	@Column(name = "is_active")
	private String isActive;

	@JsonIgnore
	@OneToMany(mappedBy = "menu")
	private List<MstMenuRoleMapping> menuRoleMappings;

	@JsonIgnore // Ignore serialization of this property
	@OneToMany(mappedBy = "menu")
	private List<MstSubMenu> subMenus;

}