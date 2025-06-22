package com.ayn.states.realstate.entity.user;

import com.ayn.states.realstate.entity.payment.Payment;
import com.ayn.states.realstate.entity.subscriptions.Subscription;
import com.ayn.states.realstate.entity.userSubscription.UserSubscription;
import com.ayn.states.realstate.enums.UserStatus;
import com.ayn.states.realstate.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ZONE_USERS")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    private String firstName;

    private String lastName;

    private String company;

    private String phone;

    private int country;

    private int governorate;

    private LocalDateTime lastSeen;

    private LocalDateTime lastLogin;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime modifiedAt;

    @Column(columnDefinition = "TINYINT default 1")
    private UserStatus isActive;

    private UserType userType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserSubscription> subscriptions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Payment> payments;



}
