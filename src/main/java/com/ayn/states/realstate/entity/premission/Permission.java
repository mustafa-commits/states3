package com.ayn.states.realstate.entity.premission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name; // e.g., "VIEW_REPORTS", "EDIT_USERS", "DELETE_PAYMENTS"

    @Column(length = 255)
    private String description;

    @Column(name = "is_active",columnDefinition = "bit(1) NOT NULL DEFAULT b'1'", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private int createdBy;

    @JsonIgnore
    private Integer updatedBy;

    @Column(name = "updated_at")
    @JsonIgnore
    private LocalDateTime updatedAt;


    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
