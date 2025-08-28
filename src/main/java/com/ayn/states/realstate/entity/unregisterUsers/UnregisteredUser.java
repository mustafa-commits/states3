package com.ayn.states.realstate.entity.unregisterUsers;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "unregistered_user")
@NoArgsConstructor
@Getter
@Setter
public class UnregisteredUser {


//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
    @Id
    @Column(name = "temp_identifier",
            nullable = false,
            unique = true,
            columnDefinition = "varchar(255)")
    private Long tempIdentifier;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt= LocalDateTime.now();

    public UnregisteredUser(long tempIdentifier) {
        this.tempIdentifier = tempIdentifier;
    }

}
