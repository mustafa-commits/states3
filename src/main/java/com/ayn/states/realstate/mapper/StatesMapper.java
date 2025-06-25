package com.ayn.states.realstate.mapper;

import com.ayn.states.realstate.dto.attachment.AttachmentDTO;
import com.ayn.states.realstate.dto.states.*;
import com.ayn.states.realstate.entity.att.Attachments;
import com.ayn.states.realstate.entity.states.States;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between States entity and DTOs
 */
@Component
public class StatesMapper {

    @Value("${StateAttLink}")
    private String stateLink;


    public StatesDTO toDto(States entity) {
        if (entity == null) {
            return null;
        }

        return StatesDTO.builder()
                .stateId(entity.getStateId())
                .description(entity.getDescription())
                .area(entity.getArea())
                .numOfRooms(entity.getNumOfRooms())
                .garageSize(entity.getGarageSize())
                .numOfBathRooms(entity.getNumOfBathRooms())
                .numOfStorey(entity.getNumOfStorey())
                .price(entity.getPrice())
                .longitude(entity.getLongitude())
                .latitude(entity.getLatitude())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .createdUser(entity.getCreatedUser())
                .modifiedUser(entity.getModifiedUser())
                .publishedBy(entity.getPublishedBy())
                .publishedAt(entity.getPublishedAt())
                .country(entity.getCountry())
                .governorate(entity.getGovernorate())
                .stateType(entity.getStateType())
                .attachments(mapAttachments(entity.getAttachments()))
                .build();
    }

    /**
     * Convert StatesCreateDTO to States entity
     */
    public States toEntity(StatesCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        States entity = new States();
        entity.setDescription(dto.getDescription());
        entity.setArea(dto.getArea());
        entity.setNumOfRooms(dto.getNumOfRooms());
        entity.setGarageSize(dto.getGarageSize());
        entity.setNumOfBathRooms(dto.getNumOfBathRooms());
        entity.setNumOfStorey(dto.getNumOfStorey());
        entity.setPrice(dto.getPrice());
        entity.setLongitude(dto.getLongitude());
        entity.setLatitude(dto.getLatitude());
        entity.setActive(true); // Default to active
        entity.setCountry(dto.getCountry());
        entity.setGovernorate(dto.getGovernorate());
        entity.setStateType(dto.getStateType());

        return entity;
    }

    /**
     * Update States entity from StatesUpdateDTO
     */
    public void updateEntityFromDto(StatesUpdateDTO dto, States entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }

        if (dto.getArea() > 0) {
            entity.setArea(dto.getArea());
        }

        if (dto.getNumOfRooms() >= 0) {
            entity.setNumOfRooms(dto.getNumOfRooms());
        }

        if (dto.getGarageSize() >= 0) {
            entity.setGarageSize(dto.getGarageSize());
        }

        if (dto.getNumOfBathRooms() >= 0) {
            entity.setNumOfBathRooms(dto.getNumOfBathRooms());
        }

        if (dto.getNumOfStorey() >= 0) {
            entity.setNumOfStorey(dto.getNumOfStorey());
        }

        if (dto.getPrice() > 0) {
            entity.setPrice(dto.getPrice());
        }

        entity.setLongitude(dto.getLongitude());
        entity.setLatitude(dto.getLatitude());
        entity.setActive(dto.isActive());
        entity.setCountry(dto.getCountry());
        entity.setGovernorate(dto.getGovernorate());

        if (dto.getStateType() != null) {
            entity.setStateType(dto.getStateType());
        }
    }

    /**
     * Convert States entity to StatesListingDTO
     */
    public StatesListingDTO toListingDto(States entity) {
        if (entity == null) {
            return null;
        }

        String thumbnailUrl = null;
        if (entity.getAttachments() != null && !entity.getAttachments().isEmpty()) {
            thumbnailUrl = entity.getAttachments().get(0).getUrlImage();
        }

        return StatesListingDTO.builder()
                .stateId(entity.getStateId())
                .description(entity.getDescription())
                .area(entity.getArea())
                .numOfRooms(entity.getNumOfRooms())
                .numOfBathRooms(entity.getNumOfBathRooms())
                .price(entity.getPrice())
                .longitude(entity.getLongitude())
                .latitude(entity.getLatitude())
                .stateType(entity.getStateType())
                .thumbnailUrl(thumbnailUrl)
                .country(entity.getCountry())
                .governorate(entity.getGovernorate())
                .build();
    }

    /**
     * Convert States entity to StatesDetailsDTO
     */
    public StatesDetailsDTO toDetailsDto(States entity) {
        if (entity == null) {
            return null;
        }

        return StatesDetailsDTO.builder()
                .stateId(entity.getStateId())
                .description(entity.getDescription())
                .area(entity.getArea())
                .numOfRooms(entity.getNumOfRooms())
                .garageSize(entity.getGarageSize())
                .numOfBathRooms(entity.getNumOfBathRooms())
                .numOfStorey(entity.getNumOfStorey())
                .price(entity.getPrice())
                .longitude(entity.getLongitude())
                .latitude(entity.getLatitude())
                .createdAt(entity.getCreatedAt())
                .country(entity.getCountry())
                .governorate(entity.getGovernorate())
                .stateType(entity.getStateType())
                .stateTypeDisplayName(entity.getStateType() != null ? entity.getStateType().toString() : null)
                .attachments(mapAttachments(entity.getAttachments()))
                .build();
    }

    /**
     * Convert list of States entities to list of StatesListingDTOs
     */
    public List<StatesListingDTO> toListingDtoList(List<States> entities) {
        return entities.stream()
                .map(this::toListingDto)
                .collect(Collectors.toList());
    }

    /**
     * Map Attachments to AttachmentDTOs
     */
    private List<AttachmentDTO> mapAttachments(List<Attachments> attachments) {
        if (attachments == null) {
            return List.of();
        }

        return attachments.stream()
                .map(attachment -> AttachmentDTO.builder()
                        .urlImage(attachment.getUrlImage())
                        .urlImageType(attachment.getUrlImageType())
                        .fullUrlImage(stateLink + attachment.getUrlImage())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Convert States entity to lightweight StatesSummaryDTO
     * @param entity States entity
     * @return StatesSummaryDTO with essential property information
     */
    public StatesSummaryDTO toSummaryDto(States entity) {
        if (entity == null) {
            return null;
        }

        String mainImageUrl = null;
        if (entity.getAttachments() != null && !entity.getAttachments().isEmpty()) {
            mainImageUrl = entity.getAttachments().get(0).getUrlImage();
        }

        return StatesSummaryDTO.builder()
                .stateId(entity.getStateId())
                .description(entity.getDescription())
                .area(entity.getArea())
                .numOfRooms(entity.getNumOfRooms())
                .numOfBathRooms(entity.getNumOfBathRooms())
                .price(entity.getPrice())
                .longitude(entity.getLongitude())
                .latitude(entity.getLatitude())
                .stateType(entity.getStateType())
                .country(entity.getCountry())
                .governorate(entity.getGovernorate())
                .mainImageUrl(mainImageUrl)
                .build();
    }

    /**
     * Convert list of States entities to list of StatesSummaryDTOs
     * @param entities List of States entities
     * @return List of StatesSummaryDTOs
     */
    public List<StatesSummaryDTO> toSummaryDtoList(List<States> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }
}
