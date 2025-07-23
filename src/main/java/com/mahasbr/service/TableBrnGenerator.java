package com.mahasbr.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class TableBrnGenerator implements BrnGeneratorService {

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${brn.generator.cache.size:10000}")
    private int cacheSize;

    private long currentMax = 0;
    private long currentCounter = 0;

    @Override
    @Transactional
    public synchronized String generateBrn(String stateCode) {
        if (currentCounter >= currentMax) {
            Number newMax = (Number) entityManager
                    .createNativeQuery("""
                        UPDATE brn_sequence_table 
                        SET next_val = next_val + :cacheSize 
                        WHERE state_code = :stateCode 
                        RETURNING next_val INTO :newMax
                    """)
                    .setParameter("cacheSize", cacheSize)
                    .setParameter("stateCode", stateCode)
                    .getSingleResult();

            currentMax = newMax.longValue();
            currentCounter = currentMax - cacheSize + 1;
        }

        long nextVal = currentCounter++;
        return String.format("%02d0000%010d", Integer.parseInt(stateCode), nextVal);
    }
}
