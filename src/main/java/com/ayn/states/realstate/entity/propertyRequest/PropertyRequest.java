package com.ayn.states.realstate.entity.propertyRequest;

import com.ayn.states.realstate.enums.Category;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "property_requests")
@Getter
@Setter
public class PropertyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private Long userId;

    private int propertyType;

    private int propertySubType;      // بيت، شقة، منزل
    private Category transactionType;    // شراء أو إيجار
    private int governorate;        // المحافظة
    private int area;           // المنطقة أو المدينة
    private int roomsCount;        // عدد الغرف
    private int priceMin;
    private int priceMax;
    private long contactPhone;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiryDate;  // تاريخ انتهاء الصلاحية
    private Boolean furnished = false;   //مفروشة
    private int areaMin;
    private int areaMax;

    @JsonIgnore
    private Integer publishedBy;

    @JsonIgnore
    private LocalDateTime publishedAt;

    @JsonIgnore
    private int createdUser;

    @JsonIgnore
    @Column(columnDefinition = "TINYINT default 1", nullable = false)
    private boolean isActive = true;

    // --- Optional: Helper method to check if request is still valid ---
    public boolean isStillValid() {
        return expiryDate == null || LocalDateTime.now().isBefore(expiryDate);
    }

}
