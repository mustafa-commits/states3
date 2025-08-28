package com.ayn.states.realstate.entity.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class NotificationMessage {

    private String recipientToken;
    private String title;
    private String body;
    private String image;
    private Map<String,String> data;

}
