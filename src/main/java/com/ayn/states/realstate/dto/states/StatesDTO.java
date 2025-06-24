package com.ayn.states.realstate.dto.states;

import com.ayn.states.realstate.dto.attachment.AttachmentDTO;
import com.ayn.states.realstate.enums.StateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Main DTO for States entity with all properties
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatesDTO {

    private long stateId;
    private String description;
    private int area;
    private int numOfRooms;
    private int garageSize;
    private int numOfBathRooms;
    private int numOfStorey;
    private long price;
    private int longitude;
    private int latitude;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int createdUser;
    private int modifiedUser;
    private int publishedBy;
    private int publishedAt;
    private int country;
    private int governorate;
    private StateType stateType;
    private List<AttachmentDTO> attachments = new ArrayList<>();
}
