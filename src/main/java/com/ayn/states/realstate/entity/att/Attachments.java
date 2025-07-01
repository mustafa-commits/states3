package com.ayn.states.realstate.entity.att;


import com.ayn.states.realstate.entity.lookup.UrlImageType;
import com.ayn.states.realstate.entity.states.States;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attachment")
public class Attachments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;


    @JsonProperty("url_image")
    @JsonIgnore
    private String UrlImage;

    @Formula("concat('http://localhost:8080/V1/api/stateAttachment/',url_image)")
    private String fullUrl;

//    @JsonProperty("url_image_type")
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private UrlImageType urlImageType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id") // Foreign key column
    @JsonBackReference // Prevents infinite recursion in JSON serialization
    private States states;


    public Attachments(String urlImage, String fullUrl, UrlImageType urlImageType, States states) {
        UrlImage = urlImage;
        this.fullUrl = fullUrl;
        this.urlImageType = urlImageType;
        this.states = states;
    }
}
