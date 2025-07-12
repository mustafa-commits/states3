package com.ayn.states.realstate.dto.states;

import com.ayn.states.realstate.dto.attachment.AttachmentDTO;
import com.ayn.states.realstate.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Detailed DTO for States entity with all properties for single property view
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatesDetailsDTO {

    private long stateId;
    private String description;
    private int area;
    private int numOfRooms;
    private int garageSize;
    private int numOfBathRooms;
    private int numOfStorey;
    private long price;
    private double longitude;
    private double latitude;
    private LocalDateTime createdAt;
    private int country;
    private int governorate;
    private Category category;
    private List<AttachmentDTO> attachments = new ArrayList<>();

    // Additional fields for detailed view
    private String formattedAddress;
    private String countryName;
    private String governorateName;
    private String stateTypeDisplayName;
}
