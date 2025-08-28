package com.ayn.states.realstate.dto.compound;

import java.util.List;

public record UnitMapDTO(
        Long id,            // null means "add new"
        String imageUrl,
        String description,
        List<String> specifications,
        boolean delete       // true means "delete this unit map"
) {}
