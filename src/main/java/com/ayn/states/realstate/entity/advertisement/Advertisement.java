package com.ayn.states.realstate.entity.advertisement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;

@Entity
@Table(name = "advertisements")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private String title;

    @Formula("CONCAT('http://72.60.81.126:8080/V1/api/CompoundAttachment/', image_url1)")
    private String imageUrl;

    @JsonIgnore
    private String imageUrl1;

    private int targetId;

    private AdvertisementType type;

    private int period;

    private Long advertiserPhone;

    private Boolean isActive = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    private int createdBy;

    public Advertisement(String title, String imageUrl, int targetId, AdvertisementType type, Boolean isActive, int createdBy,int period,Long advertiserPhone) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.targetId = targetId;
        this.type = type;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.period=period;
        this.advertiserPhone=advertiserPhone;
    }

    public boolean isStillValid() {
        LocalDateTime expiryDate = createdAt.plusDays(period);
        return LocalDateTime.now().isBefore(expiryDate);
    }
}

