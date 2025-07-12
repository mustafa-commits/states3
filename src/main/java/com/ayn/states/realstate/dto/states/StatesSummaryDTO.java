package com.ayn.states.realstate.dto.states;

import com.ayn.states.realstate.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight DTO for States entity without attachments
 * Used for listing and search results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatesSummaryDTO {
    private long stateId;
    private String description;
    private int area;
    private int numOfRooms;
    private int numOfBathRooms;
    private long price;
    private double longitude;
    private double latitude;
    private Category category;
    private int country;
    private int governorate;
    private String mainImageUrl;
}
