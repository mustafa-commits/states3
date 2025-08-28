package com.ayn.states.realstate.dto.compound;

import java.util.List;

public record CompoundDTO(
        String coverImageUrl,
        String name,
        String thumbnailUrl,
        String address,
        SocialLinksDTO socialLinks,
        String description,
        List<String> features,
        String model3dUrl,
        Double latitude,
        Double longitude,
        String contactNumber,
        boolean isActive,
        List<UnitMapDTO> unitMaps
) {}
