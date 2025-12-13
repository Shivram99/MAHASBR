package com.mahasbr.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.Menu;
import com.mahasbr.entity.Role;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

//	 List<Menu> findByParentIsNullAndActiveTrueOrderBySequenceAsc();
//
//	    @Query("""
//	        SELECT m FROM Menu m
//	        JOIN RoleMenu rm ON rm.menu.id = m.id
//	        JOIN Role r ON rm.role.id = r.id
//	        WHERE r.name = :role
//	        ORDER BY m.sequence ASC
//	    """)
//	    List<Menu> findMenusByRole(@Param("role") Role role);
	
	@Query("""
		    SELECT DISTINCT m FROM Menu m
		    LEFT JOIN FETCH m.children
		    JOIN RoleMenu rm ON rm.menu.id = m.id
		    JOIN Role r ON rm.role.id = r.id
		    WHERE r = :role
		    ORDER BY m.sequence ASC
		""")
		List<Menu> findMenusByRole(@Param("role") Role role);

	    
	    @Query("SELECT m FROM Menu m WHERE m.parent.id = :parentId")
	    List<Menu> findChildren(@Param("parentId") Long parentId);
	    
	    

	    // Menus accessible by role id
	    @Query("select rm.menu from RoleMenu rm where rm.role.id = :roleId and rm.menu.active = true")
	    List<Menu> findMenusByRoleId(@Param("roleId") Long roleId);

	    // Menus accessible by user id (from user_menus)
	    @Query("select um.menu from UserMenuEntity um where um.user.id = :userId and um.menu.active = true")
	    List<Menu> findMenusByUserId(@Param("userId") Long userId);

//	    // Helper to find direct children (used in cycle check)
//	    @Query("select m from Menu m where m.parent.id = :parentId")
//	    List<Menu> findChildren(@Param("parentId") Long parentId);
	    
	    @Query("""
	    	    SELECT DISTINCT m
	    	    FROM Menu m
	    	    LEFT JOIN FETCH m.children c
	    	    WHERE m.id IN (
	    	        SELECT rm.menu.id
	    	        FROM RoleMenu rm
	    	        JOIN rm.role r
	    	        WHERE r.name IN :roleNames
	    	    )
	    	    OR m.id IN (
	    	        SELECT parent.id
	    	        FROM Menu parent
	    	        JOIN parent.children ch
	    	        WHERE ch.id IN (
	    	            SELECT rm.menu.id
	    	            FROM RoleMenu rm
	    	            JOIN rm.role r
	    	            WHERE r.name IN :roleNames
	    	        )
	    	    )
	    	""")
	    	List<Menu> findMenusByRoleNames(@Param("roleNames") Set<String> roleNames);

	    
	    @Query("""
	    	    SELECT DISTINCT m 
	    	    FROM Menu m
	    	    JOIN RoleMenu rm ON rm.menu.id = m.id
	    	    JOIN Role r ON rm.role.id = r.id
	    	    
	    	""")
	    	List<Menu> findMenusByRoleNames();
	    @Query("""
	    	    SELECT DISTINCT m FROM Menu m
	    	    LEFT JOIN FETCH m.children c
	    	    WHERE m.parent IS NULL AND m.active = true
	    	    ORDER BY m.sequence ASC
	    	""")
	    	List<Menu> findRootMenusWithChildren();


}