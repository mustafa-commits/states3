package com.ayn.states.realstate.entity.notification.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EditNotificationRequest(@JsonProperty("notification_id") Long NotificationId,
                                      @JsonProperty("title") String Title,
                                      @JsonProperty("body") String Body
                                      //  @JsonProperty("notification_type") NotificationType notificationType,
                                      //  @JsonProperty("notification_details") Set<NotificationDetails> notificationDetails,
                                      //   Map<String,String> data

) {
}
