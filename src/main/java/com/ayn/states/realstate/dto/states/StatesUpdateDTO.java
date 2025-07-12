package com.ayn.states.realstate.dto.states;

import com.ayn.states.realstate.enums.Category;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for updating existing States
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatesUpdateDTO {

    private long stateId;
    private String description;

    @Min(value = 1, message = "Area must be greater than 0")
    private int area;

    @Min(value = 0, message = "Number of rooms cannot be negative")
    private int numOfRooms;

    @Min(value = 0, message = "Garage size cannot be negative")
    private int garageSize;

    @Min(value = 0, message = "Number of bathrooms cannot be negative")
    private int numOfBathRooms;

    @Min(value = 0, message = "Number of storeys cannot be negative")
    private int numOfStorey;

    @Min(value = 1, message = "Price must be greater than 0")
    private long price;

    private int longitude;
    private int latitude;
    private boolean isActive;
    private int country;
    private int governorate;
    private Category category;

    private List<String> imageUrlsToAdd = new ArrayList<>();
    private List<Long> attachmentIdsToRemove = new ArrayList<>();
}
