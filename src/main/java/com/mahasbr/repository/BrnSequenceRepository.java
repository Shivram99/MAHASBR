package com.mahasbr.repository;
import java.sql.CallableStatement;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class BrnSequenceRepository {

	  @PersistenceContext
	    private EntityManager entityManager;

	    public long incrementAndGetMax(String stateCode, int cacheSize) {

	        return entityManager.unwrap(Session.class).doReturningWork(connection -> {

	            String sql = """
	                DECLARE
	                    v_new_val NUMBER;
	                BEGIN
	                    UPDATE brn_sequence_table
	                       SET next_val = next_val + ?
	                     WHERE state_code = ?
	                     RETURNING next_val INTO v_new_val;
	                    
	                    ? := v_new_val;
	                END;
	            """;

	            try (CallableStatement stmt = connection.prepareCall(sql)) {

	                stmt.setInt(1, cacheSize);
	                stmt.setString(2, stateCode);
	                stmt.registerOutParameter(3, java.sql.Types.NUMERIC);

	                stmt.execute();

	                return stmt.getLong(3);
	            }
	        });
	    }
}
