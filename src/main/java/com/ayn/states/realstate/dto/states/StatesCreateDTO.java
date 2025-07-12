package com.ayn.states.realstate.dto.states;

import com.ayn.states.realstate.enums.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating new States
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatesCreateDTO {

    @NotBlank(message = "Description is required")
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

    @NotNull(message = "Country is required")
    private int country;

    @NotNull(message = "Governorate is required")
    private int governorate;

    @NotNull(message = "State type is required")
    private Category category;

//    private List<String> imageUrls = new ArrayList<>();
}
