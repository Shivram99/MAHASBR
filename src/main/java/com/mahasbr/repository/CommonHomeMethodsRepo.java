package com.mahasbr.repository;

import java.util.List;

public interface CommonHomeMethodsRepo {

	List<Object[]> findMenuNameByRoleID(Long levelRoleVal);


	List<Object[]> findSubMenuByRoleID(Long levelRoleVal);

}
