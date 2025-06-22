package com.ayn.states.realstate.entity.att;


import com.ayn.states.realstate.entity.lookup.UrlImageType;
import com.ayn.states.realstate.entity.states.States;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attachment")
public class Attachments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;


    @JsonProperty("url_image")
    private String UrlImage;

    @JsonProperty("url_image_type")
    private UrlImageType urlImageType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id") // Foreign key column
    @JsonBackReference // Prevents infinite recursion in JSON serialization
    private States states;


}
