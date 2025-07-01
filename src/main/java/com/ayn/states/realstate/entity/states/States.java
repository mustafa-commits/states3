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
@AllArgsConstructor
@NoArgsConstructor
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

    private double longitude;

    private double latitude;


    @Column(columnDefinition = "TINYINT default 1", nullable = false)
    private boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at" ,updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime modifiedAt;

    private int createdUser;

    private Integer modifiedUser;

    private Integer publishedBy;

    private LocalDateTime publishedAt;

    private int country;

    private int governorate;

    @Enumerated(EnumType.STRING)  // Use STRING instead of ORDINAL
    @Column(name = "state_type", nullable = false)
    @NotNull
    private StateType stateType;


    @OneToMany(mappedBy = "states",
            orphanRemoval = true,
            cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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

    public States(String description, int area, int numOfRooms, int garageSize, int numOfBathRooms, int numOfStorey, long price, double longitude, double latitude, int createdUser, Integer modifiedUser, Integer publishedBy, LocalDateTime publishedAt, int country, int governorate, StateType stateType) {
        this.description = description;
        this.area = area;
        this.numOfRooms = numOfRooms;
        this.garageSize = garageSize;
        this.numOfBathRooms = numOfBathRooms;
        this.numOfStorey = numOfStorey;
        this.price = price;
        this.longitude = longitude;
        this.latitude = latitude;
        this.createdUser = createdUser;
        this.modifiedUser = modifiedUser;
        this.publishedBy = publishedBy;
        this.publishedAt = publishedAt;
        this.country = country;
        this.governorate = governorate;
        this.stateType = stateType;
    }


}
