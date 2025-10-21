package com.ayn.states.realstate.entity.states;


import com.ayn.states.realstate.entity.att.Attachments;
import com.ayn.states.realstate.entity.propertyFeature.Feature;
import com.ayn.states.realstate.entity.propertyFeature.PropertyFeatures;
import com.ayn.states.realstate.enums.Category;
import com.ayn.states.realstate.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.List;

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

    private int numOfBedRooms;

    private int propertyType;

    private int propertySubType;

    private int ownershipType;

    private int buildingAge;


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

    private int country = 1;

    private int governorate;

    private String address;

    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)  // Use STRING instead of ORDINAL
    @Column(name = "state_type", nullable = false)
    @NotNull
    private Category category;


    @OneToMany(mappedBy = "states",
            orphanRemoval = true,
            cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JsonManagedReference // Prevents infinite recursion in JSON serialization
    private List<Attachments> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "featuredState",
            orphanRemoval = true,
            cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PropertyFeatures> propertyFeatures = new ArrayList<>();


    @Formula("(SELECT COUNT(DISTINCT COALESCE(ua.app_user_id, ua.unregistered_id)) FROM user_actions ua WHERE ua.state_id = state_id AND ua.action_type = 'VIEW')")
    private int viewsCount;


    public void addAttachment(Attachments attachment) {
        attachments.add(attachment);
        attachment.setStates(this);
    }

    public void removeAttachment(Attachments attachment) {
        attachments.remove(attachment);
        attachment.setStates(null);
    }

    public States(String description, int area, int numOfRooms, int garageSize, int numOfBathRooms, int numOfStorey, long price, double longitude, double latitude, int createdUser, Integer modifiedUser, Integer publishedBy, LocalDateTime publishedAt, int governorate, Category category,
                  int propertyType,int ownershipType,int buildingAge,String address, PaymentMethod paymentMethod,int propertySubType) {
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
        this.governorate = governorate;
        this.category = category;
        this.propertyType = propertyType;
        this.ownershipType = ownershipType;
        this.buildingAge = buildingAge;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.propertySubType=propertySubType;
    }


}
