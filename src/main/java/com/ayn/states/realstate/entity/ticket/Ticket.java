package com.ayn.states.realstate.entity.ticket;

import com.ayn.states.realstate.entity.ticket.DTO.TicketType;
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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ST_APP_Ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer Id;

    @JsonProperty("title")
    private String Title; ////
    @JsonIgnore
    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("create_at")
    private Date createdAt;
    @JsonIgnore
    @JsonProperty("update_at")     // todo order by
    private Date updateAt;

    @JsonProperty("modified_user")
    private Integer ModifiedUser;
    @JsonProperty("create_user")
    private Integer CreatedUser;

    @JsonProperty("is_active")
    private Boolean IsActive;

    @JsonProperty("reminder")
    @Column(columnDefinition = "int default 0")
    private int reminder;


    @JsonProperty("ticket_type")
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "integer default 1")
    private TicketType ticketType;


  /*  @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<TicketDetails> details = new HashSet<>();
*/


    @PrePersist
    private void onCreate() {
        createdAt=new Date();
    }

    public Ticket(String title, int userId, Date createdAt, Date updateAt, Integer modifiedUser, Boolean isActive,int CreatedUser,int reminder,TicketType ticketType) {
        this.Title = title;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
        this.CreatedUser=CreatedUser;
        this.ModifiedUser = modifiedUser;
        this.IsActive = isActive;
        this.reminder=reminder;
        this.ticketType=ticketType;
    }

    public Map<String, String> toMap() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Baghdad"));
        String formattedDateTime = formatter.format(createdAt);

        Map<String, String> map = new HashMap<>();
        map.put("id", Id.toString());
        map.put("title", Title);
        map.put("create_at", formattedDateTime);
        map.put("modified_user", String.valueOf(getModifiedUser()));
        map.put("created_user", String.valueOf(getCreatedUser()));
        map.put("is_active", String.valueOf(getIsActive()));
        return map;
    }
}
