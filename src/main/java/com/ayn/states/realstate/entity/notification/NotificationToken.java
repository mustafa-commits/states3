package com.ayn.states.realstate.entity.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ST_APP_notification_token")
public class NotificationToken {
    @Id
    @Column(name = "USER_ID", nullable = false)
    private Integer userId;

    private String Token;

    private TokenType tokenType;

    private Date updatedate=new Date();

    @Column(name = "NOTIFICATION_UN_READ_NO")
    private int notificationUnReadNo=0;

    @JsonProperty("is_active")
    private boolean isActive=true;



    public NotificationToken(Integer userId, String token, TokenType tokenType) {
        this.userId = userId;
        this.Token = token;
        this.tokenType = tokenType;
    }
}