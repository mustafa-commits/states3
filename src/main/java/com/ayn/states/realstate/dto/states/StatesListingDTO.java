package com.ayn.states.realstate.dto.states;

import com.ayn.states.realstate.enums.StateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simplified DTO for States entity used in listing views
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatesListingDTO {

    private long stateId;
    private String description;
    private int area;
    private int numOfRooms;
    private int numOfBathRooms;
    private long price;
    private double longitude;
    private double latitude;
    private StateType stateType;
    private String thumbnailUrl; // Main property image
    private int country;
    private int governorate;
}
