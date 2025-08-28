package com.ayn.states.realstate.entity.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ST_NOTIFICATION_DETAILS")
public class NotificationDetails {


    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @JsonProperty("send_to")
    @Column(name = "SEND_TO")
    private Long SendTo;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="NOTIFICATION_ID", nullable=false)
    private NotificationMaster notificationMaster;

    @Column(name = "SEEN_STATUS", columnDefinition = "integer (1) DEFAULT 0")
    @JsonProperty("seen_status")
    private Boolean seenStatus;

    public NotificationDetails(Long sendTo, NotificationMaster notificationMaster) {
        this.SendTo = sendTo;
        this.notificationMaster = notificationMaster;
    }
}
