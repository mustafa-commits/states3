package com.ayn.states.realstate.models.user;

import com.ayn.states.realstate.enums.LoginStatus;

public record UserCheck(LoginStatus status, String phone) {
}
