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
    private long price;
    private double longitude;
    private double latitude;
    private boolean isActive;
//    private LocalDateTime createdAt;
//    private LocalDateTime modifiedAt;
    private int createdUser;
//    private Integer modifiedUser;
//    private Integer publishedBy;
    private LocalDateTime publishedAt;
    private int country;
    private int governorate;
    private StateType stateType;
    private List<String> attachments = new ArrayList<>();
}
