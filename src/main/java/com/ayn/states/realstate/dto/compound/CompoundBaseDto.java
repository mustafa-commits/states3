package com.ayn.states.realstate.dto.compound;

public record CompoundBaseDto(
        Long id,
        String coverImageUrl,
        String name,
        int followersCount,
        String thumbnailUrl,
        String address,
        String facebook,
        String instagram,
        String twitter,
        int viewsCount,
        int propertiesCount,
        String description,
        String model3dUrl,
        Double latitude,
        Double longitude,
        String contactNumber
) {}