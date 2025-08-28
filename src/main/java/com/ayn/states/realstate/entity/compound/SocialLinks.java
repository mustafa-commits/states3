package com.ayn.states.realstate.entity.compound;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class SocialLinks {
    private String facebook;
    private String instagram;
    private String twitter;
    private String website;
}

