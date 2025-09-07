package com.ayn.states.realstate.entity.tracking;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity to track call button clicks for compounds.
 * Simplified for mobile app usage without web-specific fields.
 */
@Entity
@Table(name = "call_click_tracking", indexes = {
        @Index(name = "idx_compound_call_clicks", columnList = "compound_id"),
        @Index(name = "idx_call_click_timestamp", columnList = "clicked_at"),
        @Index(name = "idx_user_call_clicks", columnList = "app_user_id, compound_id")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CallClickTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The compound that was clicked
     */
    @Column(name = "compound_id", nullable = false)
    private Long compoundId;

    /**
     * ID of registered user who clicked (nullable for anonymous users)
     */
    @Column(name = "app_user_id")
    private Long appUserId;


    private String unRegisteredId;

    /**
     * Timestamp when the call button was clicked
     */
    @Column(name = "clicked_at", nullable = false)
    private LocalDateTime clickedAt;



    @PrePersist
    void onCreate() {
        if (clickedAt == null) {
            clickedAt = LocalDateTime.now();
        }
    }

    /**
     * Get the user identifier (registered user ID or device ID)
     */
    public String getUserIdentifier() {
        if (appUserId != null) {
            return "user_" + appUserId;
        } else if (unRegisteredId != null) {
            return "device_" + unRegisteredId;
        }
        return "unknown";
    }

    /**
     * Check if this click is from a registered user
     */
    public boolean isRegisteredUser() {
        return appUserId != null;
    }
}
