package com.ayn.states.realstate.entity.ticket;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ST_APP_TicketDetails")
public class TicketDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @JsonProperty("content")
    @Column(length = 1000)
    private String Content;

    @JsonIgnore
    private int ticketId;

    @JsonProperty("message_type")
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "integer default 0")
    private MessageType messageType;      // 0

    @JsonProperty("sender")
    private int Sender;

    @JsonProperty("receiver")
    private int Receiver;

    @JsonProperty("create_at")
    private Date createAt;

    @JsonProperty("modified_at")
    private Date ModifiedAt;

    @JsonProperty("create_user")
    private int createdUser;

    @JsonProperty("modified_user")
    private Integer ModifiedUser;

    @JsonProperty("is_active")
    private Boolean IsActive;

    @JsonProperty("seen_date")
    private Date seenDate;

    @JsonProperty("replay_id")
    private Integer replayId;

    @Transient
    @JsonProperty("message_replay")
    private String messageReplay;


//    @OneToOne(mappedBy = "ticketDetails", cascade = CascadeType.ALL)
//    private MessageReply replies;

    public TicketDetails(String content, int ticketId, MessageType messageType, int sender, int receiver, Date createAt, int CreatedUser) {
        this.Content = content;
        this.ticketId = ticketId;
        this.messageType = messageType;
        this.Sender = sender;
        this.Receiver = receiver;
        this.createAt = createAt;
        this.createdUser = CreatedUser;

    }

    public TicketDetails(String content, int ticketId, MessageType messageType, int sender, int receiver, Date createAt, int CreatedUser, Integer replayId) {
        this.Content = content;
        this.ticketId = ticketId;
        this.messageType = messageType;
        this.Sender = sender;
        this.Receiver = receiver;
        this.createAt = createAt;
        this.createdUser = CreatedUser;
        this.replayId=replayId;
    }
    public TicketDetails(String content, int ticketId, MessageType messageType, int sender, int receiver, Date createAt, int CreatedUser, Integer replayId, String messageReplay) {
        this.Content = content;
        this.ticketId = ticketId;
        this.messageType = messageType;
        this.Sender = sender;
        this.Receiver = receiver;
        this.createAt = createAt;
        this.createdUser = CreatedUser;
        this.replayId=replayId;
        this.messageReplay=messageReplay;
    }

    public TicketDetails(Integer id, String content, int ticketId, MessageType messageType, int sender, int receiver, Date createAt, Date modifiedAt, int createdUser, Integer modifiedUser, Boolean isActive, Date seenDate, Integer replayId, String messageReplay) {
        this.Id = id;
        this.Content = content;
        this.ticketId = ticketId;
        this.messageType = messageType;
        this.Sender = sender;
        this.Receiver = receiver;
        this.createAt = createAt;
        this.ModifiedAt = modifiedAt;
        this.createdUser = createdUser;
        this.ModifiedUser = modifiedUser;
        this.IsActive = isActive;
        this.seenDate = seenDate;
        this.replayId = replayId;
        this.messageReplay = messageReplay;
    }

    @PrePersist
    private void onCreate() {
        createAt = new Date();
    }


    public int getMessageType() {
        if (messageType == MessageType.TEXT)
            return 0;
        else if (messageType == MessageType.IMAGE) {
            return 1;
        } else if (messageType == MessageType.FILE) {
            return 2;
        } else if (messageType == MessageType.PDF) {
            return 3;
        } else if (messageType == MessageType.M4A) {
            return 4;
        }
        return 0;
    }


    public Map<String, String> toMap() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Baghdad"));
        String createAtFormatted = formatter.format(createAt);
        String modifiedAtFormatted = formatter.format(ModifiedAt);
        Map<String, String> map = new HashMap<>();
        map.put("details_id", Id.toString());
        map.put("content", Content);
        map.put("ticket_id", String.valueOf(ticketId));
        map.put("message_typ", String.valueOf(messageType.ordinal()));
        map.put("sender", String.valueOf(Sender));
        map.put("receiver", String.valueOf(getReceiver()));
        map.put("create_at", createAtFormatted);
        map.put("modified_at", getModifiedAt().toString());
        map.put("created_user", modifiedAtFormatted);
        return map;
    }
}
