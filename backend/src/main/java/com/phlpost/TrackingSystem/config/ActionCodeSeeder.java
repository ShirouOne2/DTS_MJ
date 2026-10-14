package com.phlpost.TrackingSystem.config;

import com.phlpost.TrackingSystem.entity.ActionCode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate's ddl-auto creates the table structure but doesn't seed data.
 * This inserts the fixed action legend once, on every startup, without
 * duplicating rows if they already exist.
 */
@Component
public class ActionCodeSeeder implements CommandLineRunner {

    @PersistenceContext
    private EntityManager em;

    private static final Object[][] CODES = {
        {"FYAA",  "For Your Appropriate Action"},
        {"FCOM",  "For Comment"},
        {"FCON",  "For Concurrence"},
        {"FS/A",  "For Signature/Approval"},
        {"FYI/F", "For Your Information/File"},
        {"FDREP", "For Draft Reply"},
        {"FMON",  "For Monitoring"},
        {"OTHR",  "Other"}
    };

    @Override
    @Transactional
    public void run(String... args) {
        for (Object[] row : CODES) {
            String code = (String) row[0];
            if (em.find(ActionCode.class, code) == null) {
                em.persist(ActionCode.builder()
                    .code(code)
                    .label((String) row[1])
                    .build());
            }
        }
    }
}
