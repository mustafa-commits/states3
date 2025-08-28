package com.ayn.states.realstate.dto.compound;

import java.util.List;

public record CompoundDetailsDto(
        CompoundBaseDto base,
        List<String> features,
        List<CompoundPostDto> posts,
        List<UnitMapDto2> unitMaps
) {}
