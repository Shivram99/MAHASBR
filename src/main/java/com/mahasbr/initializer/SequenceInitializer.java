package com.mahasbr.initializer;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class SequenceInitializer {

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${brn.generator.state.code:27}")
    private String stateCode;

    @Value("${brn.generator.cache.size:10000}")
    private int cacheSize;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void ensureSequenceExists() {
        String sequenceName = "BRN_SEQ_" + stateCode;

        try {
            // Check if the sequence exists
            Long count = ((Number) entityManager.createNativeQuery(
                    "SELECT COUNT(*) FROM USER_SEQUENCES WHERE SEQUENCE_NAME = :seqName")
                    .setParameter("seqName", sequenceName.toUpperCase())
                    .getSingleResult()).longValue();

            if (count == 0) {
                // Create the sequence dynamically
                String sql = String.format("""
                        CREATE SEQUENCE %s
                            START WITH 1
                            INCREMENT BY 1
                            CACHE %d
                            NOCYCLE
                        """, sequenceName, cacheSize);

                entityManager.createNativeQuery(sql).executeUpdate();
                System.out.println("✅ Created sequence: " + sequenceName);
            } else {
                System.out.println("ℹ️ Sequence already exists: " + sequenceName);
            }

        } catch (Exception e) {
            System.err.println("❌ Error initializing sequence: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
