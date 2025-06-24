package com.ayn.states.realstate.entity.states;


import com.ayn.states.realstate.entity.att.Attachments;
import com.ayn.states.realstate.enums.StateType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.List;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Getter
@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "STATES",indexes = {
//        @Index(name = "idx_location",columnList = "location")
//})
public class States {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stateId;

    @Column(length = 500)
    private String description;

    @Min(25)
    private int area;

    private int numOfRooms;

    private int garageSize;

    private int numOfBathRooms;

    private int numOfStorey;


    private long price;

//    @Column(columnDefinition = "POINT SRID 4326", nullable = false)
//    private Point location;

    private int longitude;

    private int latitude;


    @Column(columnDefinition = "TINYINT default 1", nullable = false)
    private boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at" ,updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime modifiedAt;

    private int createdUser;

    private int modifiedUser;

    private int publishedBy;

    private int publishedAt;

    private int country;

    private int governorate;

    @Enumerated(EnumType.STRING)  // Use STRING instead of ORDINAL
    @Column(name = "state_type", nullable = false)
    @NotNull
    private StateType stateType;


    @OneToMany(mappedBy = "states",
            orphanRemoval = true,
            cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonManagedReference // Prevents infinite recursion in JSON serialization
    private List<Attachments> attachments = new ArrayList<>();


    public void addAttachment(Attachments attachment) {
        attachments.add(attachment);
        attachment.setStates(this);
    }

    public void removeAttachment(Attachments attachment) {
        attachments.remove(attachment);
        attachment.setStates(null);
    }




}
