package com.ayn.states.realstate.entity.compound;

import com.ayn.states.realstate.entity.user.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "compound_followers")
@Getter
@Setter
public class CompoundFollower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // المستخدم
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // المجمع
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id", nullable = false)
    private Compound compound;

    // هل يريد إشعارات
    private boolean notificationsEnabled;

    // تاريخ بدء المتابعة
    private LocalDateTime followedAt;

    public CompoundFollower(Users user, Compound compound, boolean notificationsEnabled, LocalDateTime followedAt) {
        this.user = user;
        this.compound = compound;
        this.notificationsEnabled = notificationsEnabled;
        this.followedAt = followedAt;
    }
}
