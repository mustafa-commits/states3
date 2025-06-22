package com.ayn.states.realstate.entity.lookup;

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
@Table(
        name = "lookup",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"type_code", "code"})
        }
)
public class LookUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private int code;

    private String value;

    @Column(name = "type_code")
    private int typeCode;

    @Column(name = "label", length = 255, nullable = false)
    private String label;

    private int sortOrder;

    @Column(columnDefinition = "TINYINT default 1")
    private boolean isActive;



}
