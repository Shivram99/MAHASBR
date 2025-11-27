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
        String seqName = ("BRN_SEQ_" + stateCode).toUpperCase();

        try {
            Long count = ((Number) entityManager.createNativeQuery(
                    "SELECT COUNT(*) FROM USER_SEQUENCES WHERE SEQUENCE_NAME = :seq")
                    .setParameter("seq", seqName)
                    .getSingleResult()).longValue();

            if (count == 0) {

                String sql = """
                    CREATE SEQUENCE %s
                      START WITH 1
                      INCREMENT BY 1
                      CACHE %d
                      NOCYCLE
                """.formatted(seqName, cacheSize);

                entityManager.createNativeQuery(sql).executeUpdate();
                System.out.println("✅ Created Oracle Sequence: " + seqName);
            } else {
                System.out.println("ℹ️ Sequence already exists: " + seqName);
            }

        } catch (Exception e) {
            System.err.println("❌ Error creating sequence: " + e.getMessage());
        }
    }
}
