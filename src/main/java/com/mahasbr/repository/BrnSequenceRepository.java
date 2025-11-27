package com.mahasbr.repository;

import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class BrnSequenceRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public long getNextVal(String stateCode) {
		String seqName = "BRN_SEQ_" + stateCode;

		return ((Number) entityManager.createNativeQuery("SELECT " + seqName + ".NEXTVAL FROM DUAL").getSingleResult())
				.longValue();
	}
}
