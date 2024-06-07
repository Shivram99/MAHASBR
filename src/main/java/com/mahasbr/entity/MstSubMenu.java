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
@NoArgsConstructor
@Table(name = "sub_menu_mst")
public class MstSubMenu {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mstsubmenu_seq_generator")
	@SequenceGenerator(name = "mstsubmenu_seq_generator", sequenceName = "mstsubmenu_seq", allocationSize = 1)
	@Column(name = "SUB_MENU_ID")
	private Long subMenuId;
	/*
	 * @ManyToMany(fetch = FetchType.LAZY)
	 * 
	 * @JoinTable(name = "MstMenu", joinColumns = @JoinColumn(name = "MENU_ID"),
	 * inverseJoinColumns = @JoinColumn(name = "MENU_ID")) Set<MstMenu> mstmenu =
	 * new HashSet<>();
	 * 
	 * @ManyToMany(fetch = FetchType.LAZY)
	 * 
	 * @JoinTable(name = "roles", joinColumns = @JoinColumn(name = "id"),
	 * inverseJoinColumns = @JoinColumn(name = "id")) private Set<Role> role = new
	 * HashSet<>();
	 */

	@Column(name = "sub_menu_name_english")
	private String subMenuNameEnglish;

	@Column(name = "controller_name")
	private String controllerName;

	@Column(name = "link_name")
	private String linkName;

	@Column(name = "sub_menu_name_marathi")
	private String subMenuNameMarathi;

	@Column(name = "is_active")
	private Character isActive;

}
