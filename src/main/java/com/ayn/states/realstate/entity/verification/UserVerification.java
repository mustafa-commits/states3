package com.ayn.states.realstate.entity.verification;

import com.ayn.states.realstate.enums.CodeSend;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "APP_UserVerification")
public class UserVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APP_UserVerification_seq")
    @SequenceGenerator(name = "APP_UserVerification_seq", sequenceName = "APP_UserVerification_seq", allocationSize = 1)
    private Integer Id;

    private int userId;

    private int secret;

    private Date createDate;

    private CodeSend whatsApp;

    private String phone;


    public UserVerification(int userId, int secret, CodeSend whatsApp,String phone) {
        this.userId = userId;
        this.secret = secret;
        this.whatsApp=whatsApp;
        this.phone=phone;
    }

    @PrePersist
    private void onCreate() {
        createDate = new Date();
    }


}
