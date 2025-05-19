package com.ayn.states.realstate.models.user;

import com.ayn.states.realstate.enums.CodeSend;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

public record UserNumberCheckRequest (@Size(min = 6, max = 17, message = "Number length must be between 10 and 17")
                                     String number, @JsonProperty("country_code") String countryCode,
                                     @JsonProperty("code_send_platform") CodeSend codeSend) {
}
