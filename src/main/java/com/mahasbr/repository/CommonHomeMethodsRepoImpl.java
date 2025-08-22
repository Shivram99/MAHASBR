package com.mahasbr.repository;

import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SuppressWarnings("unchecked")
@Repository
public class CommonHomeMethodsRepoImpl implements CommonHomeMethodsRepo {
	// protected final Log logger = LogFactory.getLog(getClass());
	@PersistenceContext
	EntityManager manager;

	@Override
	public List<Object[]> findRoleLevelMstList() {

		Session currentSession = manager.unwrap(Session.class);
		String hql = "SELECT " + "a.id, " + "a.role_id, " + "a.role_name, " + "a.role_description " + "FROM "
				+ "role_mst a where a.role_id in('1','2','5','6','8','9','10','11','12','13','14','15','16','17','18','19','20','21','22','23','24','25','26','27','29','30','31','32','33','34') "
				+ "ORDER BY " + "a.id ";
	    Query<Object[]> query = currentSession.createNativeQuery(hql,Object[].class);
	    return query.getResultList();
	}

	@Override
	public List<Object[]> findMenuNameByRoleID(Set<Long> roleIds) {  // Pass Set of Role IDs, not Role objects

	    Session currentSession = manager.unwrap(Session.class);

	    String sql = """
	        SELECT 
	            a.menu_code, 
	            a.menu_name_english, 
	            a.menu_name_marathi,
	            a.icon
	        FROM 
	            menu_mst a
	        JOIN 
	            menu_role_mapping b 
	            ON a.menu_code = b.menu_code
	        WHERE  
	            b.role_Id IN (:roleIds)
	            AND b.is_active = '1'
	        ORDER BY 
	            a.menu_code, 
	            a.menu_name_english, 
	            a.menu_name_marathi
	    """;

	    Query<Object[]> query = currentSession.createNativeQuery(sql, Object[].class);
	    query.setParameter("roleIds", roleIds);
	    return query.getResultList();
	}



	@Override
	public List<Object[]> findSubMenuByRoleID(Set<Long> roleIds) {
		Session currentSession = manager.unwrap(Session.class);
		String hql = "select sub_menu_id,menu_code,role_id,sub_menu_name_english,sub_menu_name_marathi,	controller_name,link_name  from sub_menu_mst where role_Id IN (:roleIds) and is_active='" + 1 + "' order by sub_menu_id ";
		Query<Object[]> query = currentSession.createNativeQuery(hql,Object[].class);
	    query.setParameter("levelRoleVal", roleIds);
	    return query.getResultList();

	}

	@Override
	public List<Object[]> findAllMenu() {
		Session currentSession = manager.unwrap(Session.class);
		String hql = "select a.menu_id,a.menu_code, a.menu_name_english,a.menu_name_marathi,a.is_active,a.icon from menu_mst a order by a.menu_id";
		Query<Object[]> query = currentSession.createNativeQuery(hql,Object[].class);
	    return query.getResultList();

	}

	@Override
	public List<Object[]> findAllRole() {
		Session currentSession = manager.unwrap(Session.class);
		String hql = "select a.role_id as id, a.role_id,a.role_name, a.role_description,a.is_active from role_mst a order by a.role_id";
		Query<Object[]> query = currentSession.createNativeQuery(hql,Object[].class);
	    return query.getResultList();

	}
	@Override
	public List<Object[]> findAllSubMenu() {
	    Session currentSession = manager.unwrap(Session.class);
	    String hql = """
	        SELECT 
	            a.sub_menu_id, 
	            d.role_name, 
	            b.menu_name_english, 
	            a.sub_menu_name_english,
	            a.sub_menu_name_marathi,
	            a.controller_name,
	            a.link_name,
	            a.is_active, 
	            b.menu_code AS menu_code, 
	            d.role_id,
	            a.icon
	        FROM 
	            sub_menu_mst a
	        JOIN 
	            menu_mst b ON a.menu_code = b.menu_code
	        LEFT JOIN 
	            menu_role_mapping c ON a.role_id = c.role_id AND b.menu_code = c.menu_code
	        JOIN 
	            role_mst d ON a.role_id = d.role_id 
	        ORDER BY 
	            a.sub_menu_id, d.role_name
	        """;

	    Query<Object[]> query = currentSession.createNativeQuery(hql,Object[].class);
	    return query.getResultList();
	}


	@Override
	public List<Object[]> findAllMenuRoleMapping() {
		Session currentSession = manager.unwrap(Session.class);
		String hql = "select c.menu_map_id,a.menu_name_english,a.menu_name_marathi,b.role_name,c.is_active, a.menu_code as menu_code, b.role_id FROM "
				+ "menu_mst a , role_mst b, menu_role_mapping c "
				+ "WHERE a.menu_code = c.menu_code AND b.role_id = c.role_id ORDER BY c.menu_map_id";
		Query<Object[]> query = currentSession.createNativeQuery(hql,Object[].class);
	    return query.getResultList();
	}

//	@Override
//	public MstRoleEntity findMstRoleId(int roleId) {
//		MstRoleEntity objDept = null;
//		Session currentSession = manager.unwrap(Session.class);
//		objDept = currentSession.get(MstRoleEntity.class, roleId);
//		return objDept;
//	}
//
//	@Override
//	public void updateMstRoleStatus(MstRoleEntity objDeptForReject) {
//		Session currentSession = manager.unwrap(Session.class);
//		currentSession.merge(objDeptForReject);
//
//	}
//
//	@Override
//	public MstRoleEntity findroleById(Integer roleId) {
//		// TODO Auto-generated method stub
//
//		MstRoleEntity objrole = null;
//		Session currentSession = manager.unwrap(Session.class);
//		objrole = currentSession.get(MstRoleEntity.class, roleId);
//		return objrole;
//
//	}
//
//	@Override
//	public void updaterole(MstRoleEntity objrole) {
//		// TODO Auto-generated method stub
//		Session currentSession = manager.unwrap(Session.class);
//		currentSession.merge(objrole);
//
//	}
//
//	@Override
//	public int saveMstRole(MstRoleEntity mstRoleEntity) {
//		Session currentSession = manager.unwrap(Session.class);
//		MstRoleEntity merge = currentSession.merge(mstRoleEntity);
//		return merge.getRoleId();
//	}

	@Override
	public List<Object[]> findMenuMappedToUsername(String userName) {
	    Session currentSession = manager.unwrap(Session.class);
	    String hql = """
		        SELECT 
				 distinct
		            a.menu_code, 
		            a.menu_name_english, 
		            a.menu_name_marathi,
		            a.icon
		        FROM 
		            menu_mst a
		        JOIN 
		            user_menu_mapping b 
		            ON a.menu_code = b.menu_code
		        WHERE  
		            b.user_name = :userName
		            AND a.is_active = '1'
		        GROUP BY 
		            a.menu_code, 
		            a.menu_name_english, 
		            a.menu_name_marathi,
		            a.icon
		        ORDER BY 
		            a.menu_code, 
		            a.menu_name_english, 
		            a.menu_name_marathi
		    """;
	    Query<Object[]> query = currentSession.createNativeQuery(hql,Object[].class);
	    query.setParameter("userName", userName);
	    return query.getResultList();
	}

	@Override
	public List<Object[]> findSubMenuByUserName(String userName) {
	    Session currentSession = manager.unwrap(Session.class);
	    String hql = """
		        SELECT 
		            a.sub_menu_id, 
		            a.menu_code, 
		            a.role_id,
		            a.sub_menu_name_english,
		            a.sub_menu_name_marathi,
		            a.controller_name,
		            a.link_name
		        FROM 
		            sub_menu_mst a
		        JOIN   
		            user_menu_mapping b 
		            ON a.sub_menu_code = b.sub_menu_code
		        WHERE  
		            b.user_name = :userName
		            AND a.is_active = '1'
		        ORDER BY 
		             a.sub_menu_id
		    """;
	    Query<Object[]> query = currentSession.createNativeQuery(hql,Object[].class);
	    query.setParameter("userName", userName);
	    return query.getResultList();
	}
	
}