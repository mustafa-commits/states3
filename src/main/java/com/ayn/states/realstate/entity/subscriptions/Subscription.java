package com.ayn.states.realstate.entity.subscriptions;


import com.ayn.states.realstate.entity.user.Users;
import com.ayn.states.realstate.enums.PlanDuration;
import com.ayn.states.realstate.enums.PlanType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "SUBSCRIPTION_PLAN")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long subId;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanDuration duration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType planType;

    // Features
    private Integer maxPropertyListings;
    private Integer maxPhotosPerProperty;
    private Boolean featuredListings;
    private Boolean prioritySupport;
    private Boolean analyticsAccess;
    private Boolean dashboardAccess;

    @Column(nullable = false,columnDefinition = "bit(1) NOT NULL DEFAULT b'1'")
    private Boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

//    @OneToMany(mappedBy = "subscriptionPlan", cascade = CascadeType.ALL)
//    private List<Users> userSubscriptions;



    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Subscription(String name, String description, BigDecimal price,
                            PlanDuration duration, PlanType planType) {
        this();
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.planType = planType;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}
