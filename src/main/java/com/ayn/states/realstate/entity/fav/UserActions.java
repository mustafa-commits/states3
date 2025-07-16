package com.ayn.states.realstate.entity.fav;


import com.ayn.states.realstate.entity.states.States;
import com.ayn.states.realstate.entity.unregisterUsers.UnregisteredUser;
import com.ayn.states.realstate.entity.user.Users;
import com.ayn.states.realstate.enums.ActionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_actions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserActions {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = true)
    @JoinColumn(name = "app_user_id")
    private Users user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "state_id", nullable = false)
    private States state;

    @ManyToOne(optional = true)
    @JoinColumn(name = "unregistered_id")
    UnregisteredUser unregisteredUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Column(name = "action_time", nullable = false, updatable = false)
    private LocalDateTime actionTime;

    public UserActions( States state, UnregisteredUser unregisteredUser, ActionType actionType, LocalDateTime actionTime) {
        this.state = state;
        this.unregisteredUser = unregisteredUser;
        this.actionType = actionType;
        this.actionTime = actionTime;
    }

    public UserActions(Users user, States state, ActionType actionType, LocalDateTime actionTime) {
        this.user = user;
        this.state = state;
        this.actionType = actionType;
        this.actionTime = actionTime;
    }
}
