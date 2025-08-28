package com.ayn.states.realstate.dto.states;

import com.ayn.states.realstate.enums.Category;
import com.ayn.states.realstate.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatesDTO2 {

    private Long stateId;
    private String description;
    private int area;
    private int numOfRooms;
    private int garageSize;
    private int numOfBathRooms;
    private int numOfStorey;
    private int numOfBedrooms;
    private String propertyType;
    private String propertySubType;
    private String ownershipType;
    private int buildingAge;
    private long price;
    private double longitude;
    private double latitude;
    private LocalDateTime publishedAt;
    private String country;
    private String governorate;
    private Category category;
    private String address;
    private PaymentMethod paymentMethod;
    private List<String> attachments = new ArrayList<>();

    private List<String> features = new ArrayList<>();

    private String publisherName;
    private String publisherPhone;

    private int viewCount;
    private boolean isFavorite;

    private int favCount;

    private int distance;


}
