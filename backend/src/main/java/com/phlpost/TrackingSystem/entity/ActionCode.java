package com.phlpost.TrackingSystem.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Lookup table for the fixed action legend (FYAA, FCOM, FCON, FS/A, FYI/F,
 * FDREP, FMON, OTHR). Seed data lives in DataSeeder, not here — see the
 * project README for the CommandLineRunner that inserts these on startup.
 */
@Entity
@Table(name = "action_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionCode {

    @Id
    @Column(length = 10)
    private String code;

    @Column(nullable = false, length = 255)
    private String label;

    @Column(name = "default_text", columnDefinition = "text")
    private String defaultText;
}
