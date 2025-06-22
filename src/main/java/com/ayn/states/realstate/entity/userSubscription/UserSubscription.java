package com.ayn.states.realstate.entity.userSubscription;

import com.ayn.states.realstate.entity.payment.Payment;
import com.ayn.states.realstate.entity.subscriptions.Subscription;
import com.ayn.states.realstate.entity.user.Users;
import com.ayn.states.realstate.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_subscriptions")
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private Subscription subscriptionPlan;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status; // ACTIVE, EXPIRED, CANCELLED, SUSPENDED

    private Boolean autoRenew = true;

    // Usage tracking
    private Integer currentPropertyListings = 0;
    private Integer totalPropertyListings = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "userSubscription", cascade = CascadeType.ALL)
    private List<Payment> payments;
}
