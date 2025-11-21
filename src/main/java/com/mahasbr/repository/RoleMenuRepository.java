package com.mahasbr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mahasbr.entity.Menu;
import com.mahasbr.entity.Role;
import com.mahasbr.entity.RoleMenu;

public interface RoleMenuRepository extends JpaRepository<RoleMenu, Long> {
	List<RoleMenu> findByRole_Id(Long roleId);
	
	void deleteByRole_IdAndMenu_IdIn(Long roleId, List<Long> menuIds);

	void deleteByRole(Role role);

	@Query("SELECT m FROM Menu m JOIN RoleMenu rm ON rm.menu.id = m.id WHERE rm.role.id = :roleId")
	List<Menu> findMenusByRoleId(@Param("roleId") Long roleId);

	@Query("SELECT m FROM Menu m JOIN UserMenuEntity um ON um.menu.id = m.id WHERE um.user.id = :userId")
	List<Menu> findMenusByUserId(@Param("userId") Long userId);
}
