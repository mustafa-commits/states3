package com.ayn.states.realstate.entity.notification.DTO;

import com.ayn.states.realstate.entity.notification.NotificationDetails;
import com.ayn.states.realstate.entity.notification.NotificationType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Set;

public record SendNotificationRequest(String Title,
                                      String Body,
                                      @JsonProperty("notification_type") NotificationType notificationType,
                                      @JsonProperty("notification_details") Set<NotificationDetails> notificationDetails,
                                      String image,
                                      Map<String,String> data
                                      ) {
}
