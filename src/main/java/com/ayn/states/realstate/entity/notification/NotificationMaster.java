package com.ayn.states.realstate.entity.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ST_NOTIFICATION_MASTER")
public class NotificationMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @JsonProperty("title")
    private String Title;

    @JsonProperty("body")
    private String Body;

    @JsonProperty("create_at")
    private Date CreateAt;

    //@JsonProperty("modified_at")
    @JsonIgnore
    private Date modifiedAt;

    //@JsonProperty("create_user")
    @JsonIgnore
    private Integer CreatedUser;

    //@JsonProperty("modified_user")
    @JsonIgnore
    private Integer ModifiedUser;

    //@JsonProperty("is_active")
    @JsonIgnore
    private Boolean IsActive;

    @Column(columnDefinition = "integer default 0")
    @JsonIgnore
    private NotificationType notificationType;

    @OneToMany(mappedBy="notificationMaster")
    @JsonIgnore
    private Set<NotificationDetails> notificationDetails;

    @Transient
    @JsonProperty("seen_no")
    private Long seenNo;

    public NotificationMaster(String title, String body, Date createAt, Date modifiedAt, Integer createdUser, Integer modifiedUser, Boolean isActive, NotificationType notificationType, Set<NotificationDetails> notificationDetails) {
        this.Title = title;
        this.Body = body;
        this.CreateAt = createAt;
        this.modifiedAt = modifiedAt;
        this.CreatedUser = createdUser;
        this.ModifiedUser = modifiedUser;
        this.IsActive = isActive;
        this.notificationType = notificationType;
        this.notificationDetails = notificationDetails;
    }
}
