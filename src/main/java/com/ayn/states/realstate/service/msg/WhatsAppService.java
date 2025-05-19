package com.ayn.states.realstate.service.msg;

import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhatsAppService {


    public boolean sendMessage(String phoneNumber, String messageBody) {
        String token = "c97bs5z65hvpkw6b";
        String instanceId = "instance41001";


        OkHttpClient client = new OkHttpClient();
        // Build the request body
        RequestBody body = new FormBody.Builder()
                .add("token", token)
                .add("to", "+" + phoneNumber)
                .add("body", messageBody)
                .add("priority", "10")
                .add("referenceId", "")
                .add("msgId", "")
                .add("mentions", "")
                .build();

        // Build the request
        Request request = new Request.Builder()
                .url("https://api.ultramsg.com/" + instanceId + "/messages/chat")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        try {
            client.newCall(request).execute().body().string();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
