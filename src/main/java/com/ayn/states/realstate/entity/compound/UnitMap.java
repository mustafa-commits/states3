package com.ayn.states.realstate.entity.compound;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "unit_maps")
//@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnitMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @ElementCollection
    private List<String> imageUrl; // صورة المخطط

    private String description; // وصف الصورة

    // مواصفات الخارطة (المساحة، عدد الغرف... إلخ)
    @ElementCollection
    @CollectionTable(name = "unit_map_specs", joinColumns = @JoinColumn(name = "unit_map_id"))
    @Column(name = "spec")
    private List<String> specifications;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id")
    @JsonIgnore
    private Compound compound;


    public List<String> getImageUrl() {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return Collections.emptyList();
        }

        return imageUrl.stream()
                .map(p -> "http://31.97.185.232:8080/V1/api/CompoundAttachment/" + p)
                .collect(Collectors.toList());
    }

    public String getDescription() {
        return description;
    }

    public List<String> getSpecifications() {
        return specifications;
    }
}
