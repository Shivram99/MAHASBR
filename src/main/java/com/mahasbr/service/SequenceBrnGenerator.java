package com.mahasbr.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class SequenceBrnGenerator implements BrnGeneratorService {

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${brn.generator.cache.size:10000}")
    private int cacheSize;

    @Override
    @Transactional
    public String generateBrn(String stateCode) {
        String sequenceName = "brn_seq_" + stateCode;
        Long nextVal = ((Number) entityManager
                .createNativeQuery("SELECT " + sequenceName + ".NEXTVAL FROM dual")
                .getSingleResult()).longValue();

        // Format: SS0000XXXXXXXXXX (16-digit)
        String formatted = String.format("%02d0000%010d", Integer.parseInt(stateCode), nextVal);
        return formatted;
    }
}
