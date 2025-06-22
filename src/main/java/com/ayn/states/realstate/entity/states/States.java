package com.ayn.states.realstate.entity.states;


import com.ayn.states.realstate.entity.att.Attachments;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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
@Table(name = "STATES",indexes = {
        @Index(name = "idx_location",columnList = "location")
})
public class States {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long stateId;

    private String description;

    private int area;

    private int numOfRooms;

    private int garageSize;

    private int numOfBathRooms;

    private int numOfStorey;


    private long price;

    @Column(columnDefinition = "POINT SRID 4326", nullable = false)
    private Point location;


    @Column(columnDefinition = "TINYINT default 1")
    private boolean isActive;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime modifiedAt;

    private int createdUser;

    private int modifiedUser;

    private int publishedBy;

    private int publishedAt;

    private int country;

    private int governorate;


    @OneToMany(mappedBy = "states", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Prevents infinite recursion in JSON serialization
    private List<Attachments> attachments = new ArrayList<>();



}
