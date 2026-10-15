package com.philpost.dts.website.entity;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "action_codes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ActionCode {

    @Id
    @Column(length = 10)
    private String code;

    @Column(nullable = false, length = 255)
    private String label;

    @Column(name = "requires_signature", nullable = false)
    private boolean requiresSignature;

    @Column(name = "is_advisory", nullable = false)
    private boolean advisory;
}