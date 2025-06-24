package com.ayn.states.realstate.dto.states;

import com.ayn.states.realstate.enums.StateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for property search criteria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateSearchCriteriaDTO {

    private StateType stateType;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer minArea;
    private Integer maxArea;
    private Integer minRooms;
    private Integer minBathRooms;
    private Integer country;
    private Integer governorate;
    private Integer radius; // Search radius in km
    private Integer latitude; // Center point for radius search
    private Integer longitude; // Center point for radius search
    private String keyword; // For text search in description
    private Boolean isActive;

    // Pagination params
    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDirection = "desc";
}
