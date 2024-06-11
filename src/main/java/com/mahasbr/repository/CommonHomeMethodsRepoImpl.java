package com.mahasbr.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class CommonHomeMethodsRepoImpl implements CommonHomeMethodsRepo {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public List<Object[]> findMenuNameByRoleID(Long levelRoleVal) {
		String hql = "SELECT a.menu_code, " + "a.menu_name_english, " + "a.menu_name_marathi " + "FROM   menu_mst a, "
				+ "menu_role_mapping b " + "WHERE  a.menu_code = b.menu_code " + "AND b.role_Id = '" + levelRoleVal
				+ "' " + "AND b.is_active = '1' " + "GROUP  BY a.menu_code, " + "a.menu_name_english, "
				+ "a.menu_name_marathi " + "ORDER  BY a.menu_code, " + "a.menu_name_english, "
				+ "a.menu_name_marathi; ";
		Session session = entityManager.unwrap(Session.class);
		Query query = session.createNativeQuery(hql);
		return query.list();
	}

	@Override
	public List<Object[]> findSubMenuByRoleID(Long levelRoleVal) {
		Session currentSession = entityManager.unwrap(Session.class);
		String hql = "select sub_menu_id,menu_code,role_id,sub_menu_name_english,sub_menu_name_marathi,	controller_name,link_name  from sub_menu_mst where role_Id='"
				+ levelRoleVal + "' and is_active='" + 1 + "' order by sub_menu_id ";
		Query query = currentSession.createNativeQuery(hql);
		return (List<Object[]>) query.list();
	}

}
