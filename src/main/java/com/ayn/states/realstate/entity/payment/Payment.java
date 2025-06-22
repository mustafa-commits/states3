package com.ayn.states.realstate.entity.payment;

import com.ayn.states.realstate.entity.user.Users;
import com.ayn.states.realstate.entity.userSubscription.UserSubscription;
import com.ayn.states.realstate.enums.PaymentMethod;
import com.ayn.states.realstate.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_subscription_id", nullable = false)
    private UserSubscription userSubscription;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status; // PENDING, COMPLETED, FAILED, REFUNDED

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // CREDIT_CARD, PAYPAL, BANK_TRANSFER

    private String transactionId; // External payment gateway transaction ID
    private String paymentGateway; // STRIPE, PAYPAL, etc.

    private String description;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;

}
