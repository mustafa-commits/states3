package com.ayn.states.realstate.entity.fav;


import com.ayn.states.realstate.entity.compound.Compound;
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
@Table(name = "user_actions", indexes = {
        @Index(name = "idx_user_actions_state_id", columnList = "state_id"),
        @Index(name = "idx_user_actions_compound_id", columnList = "compound_id"),
        @Index(name = "idx_user_actions_action_type", columnList = "action_type"),
        @Index(name = "idx_user_actions_action_time", columnList = "action_time"),
        @Index(name = "idx_user_actions_user_id", columnList = "app_user_id")
})
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
    @JoinColumn(name = "state_id", nullable = true)
    private States state;

    @ManyToOne(optional = true)
    @JoinColumn(name = "compound_id", nullable = true)
    private Compound compound;

    @ManyToOne(optional = true)
    @JoinColumn(name = "unregistered_id",
            referencedColumnName = "temp_identifier",
            columnDefinition = "varchar(255)")
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


    public static UserActions createStateView(States state, Users user) {
        UserActions action = new UserActions();
        action.setState(state);
        action.setUser(user);
        action.setActionType(ActionType.VIEW);
        action.setActionTime(LocalDateTime.now());
//        action.setIpAddress(ipAddress);
//        action.setUserAgent(userAgent);
//        action.setSessionId(sessionId);
        return action;
    }

    public static UserActions createCompoundView(Compound compound, Users user) {
        UserActions action = new UserActions();
        action.setCompound(compound);
        action.setUser(user);
        action.setActionType(ActionType.VIEW);
        action.setActionTime(LocalDateTime.now());
//        action.setIpAddress(ipAddress);
//        action.setUserAgent(userAgent);
//        action.setSessionId(sessionId);
        return action;
    }

    public static UserActions createStateViewUnregistered(States state, UnregisteredUser unregisteredUser) {
        UserActions action = new UserActions();
        action.setState(state);
        action.setUnregisteredUser(unregisteredUser);
        action.setActionType(ActionType.VIEW);
//        action.setIpAddress(ipAddress);
//        action.setUserAgent(userAgent);
//        action.setSessionId(sessionId);
        return action;
    }

    public static UserActions createCompoundViewUnregistered(Compound compound, UnregisteredUser unregisteredUser, String ipAddress, String userAgent, String sessionId) {
        UserActions action = new UserActions();
        action.setCompound(compound);
        action.setUnregisteredUser(unregisteredUser);
        action.setActionType(ActionType.VIEW);
//        action.setIpAddress(ipAddress);
//        action.setUserAgent(userAgent);
//        action.setSessionId(sessionId);
        return action;
    }
}
