package com.ayn.states.realstate.dto.states;

import com.ayn.states.realstate.dto.feature.FeatureDto;
import com.ayn.states.realstate.enums.Category;
import com.ayn.states.realstate.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatesDTO {

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
//    private boolean isActive;

//    private int createdUser;
//    private Integer modifiedUser;
//    private Integer publishedBy;
    private LocalDateTime publishedAt;
//    private String country;
    private String governorate;
    private Category category;
    private String address;
    private PaymentMethod paymentMethod;
    private List<String> attachments = new ArrayList<>();

    private List<FeatureDto> features = new ArrayList<>();

    private String publisherName;
    private String publisherPhone;

    private int viewCount;
    private boolean isFavorite;

    private int favCount;

    // Custom setter to parse the JSON string from SQL
    public void setFeaturesJson(String featuresJson) {
        if (featuresJson != null && !featuresJson.isEmpty()) {
            this.features = Arrays.stream(featuresJson.split("\\|\\|\\|"))
                    .map(json -> {
                        try {
                            // Simple JSON parsing
                            String name = json.substring(json.indexOf("\"name\":\"") + 8, json.indexOf("\",\"imageUrl\""));
                            String imageUrl = json.substring(json.indexOf("\"imageUrl\":\"") + 12, json.lastIndexOf("\""));
                            return new FeatureDto(name, imageUrl);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }

}
