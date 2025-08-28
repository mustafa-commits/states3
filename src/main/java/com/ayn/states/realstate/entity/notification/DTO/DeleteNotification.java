package com.ayn.states.realstate.entity.notification.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteNotification(@JsonProperty("notification_id") Long notificationId) {
}
