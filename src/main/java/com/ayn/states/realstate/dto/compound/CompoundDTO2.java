package com.ayn.states.realstate.dto.compound;

import com.ayn.states.realstate.entity.compound.Compound;

public record CompoundDTO2(
        Compound compound,
        boolean followedByUser
) {}
