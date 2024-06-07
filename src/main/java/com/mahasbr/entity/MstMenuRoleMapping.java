
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
@Table(name = "menu_role_mapping")
@NoArgsConstructor
public class MstMenuRoleMapping extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mstmenurolemapping_seq_generator")
	@SequenceGenerator(name = "mstmenurolemapping_seq_generator", sequenceName = "mstMenuRoleMapping_seq", allocationSize = 1)
	@Column(name = "MENU_MAP_ID")
	private Long menuMapID;

	/*
	 * @ManyToMany
	 * 
	 * @JoinTable(name = "mst_menu_role_mapping", joinColumns = @JoinColumn(name =
	 * "MENU_MAP_ID"), inverseJoinColumns = @JoinColumn(name = "MENU_ID"))
	 * Set<MstMenu> mstMenu = new HashSet<>();
	 * 
	 * @ManyToMany
	 * 
	 * @JoinTable(name = "mst_menu_role_mapping", joinColumns = @JoinColumn(name =
	 * "MENU_MAP_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID")) Set<Role>
	 * role = new HashSet<>();
	 */
	@Column(name = "is_active")
	private Character isActive;

	public MstMenuRoleMapping(Character isActive) {
		// this.mstMenu = mstMenu;
		// this.role = role;
		this.isActive = isActive;
	}
}