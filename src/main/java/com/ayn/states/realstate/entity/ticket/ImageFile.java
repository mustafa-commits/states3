package com.ayn.states.realstate.entity.ticket;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ST_APP_ImageFile")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ImageFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    private String name;
    private String type;


}