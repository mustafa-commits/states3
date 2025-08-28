package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.entity.notification.DTO.AddToken;
import com.ayn.states.realstate.entity.notification.DTO.SendNotificationRequest;
import com.ayn.states.realstate.entity.notification.NotificationMaster;
import com.ayn.states.realstate.entity.notification.NotificationMessage;
import com.ayn.states.realstate.entity.notification.NotificationToken;
import com.ayn.states.realstate.repository.notification.NotificationMasterRepo;
import com.ayn.states.realstate.service.notification.NotificationService;
import com.ayn.states.realstate.service.token.TokenService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
public class NotificationController implements SecuredRestController {

    @Autowired
    NotificationService notificationService;

    @Autowired
    TokenService tokenService;

    @Autowired
    NotificationMasterRepo notificationMasterRepo;


//    @PostMapping("/V1/api/SendNotification")
    public String sendNotification(@RequestBody NotificationMessage notificationMessage){
        return notificationService.sendNotificationByToken(notificationMessage);
    }

    @PostMapping("/V1/api/AddToken")
    public NotificationToken AddToken(@Validated @RequestHeader(name = "Authorization") String token, @RequestBody AddToken addToken) {
        return notificationService.AddToken(addToken,token);
    }


    @PostMapping("/V1/api/SendNotification")
    public NotificationMaster SendNotification(@RequestBody SendNotificationRequest sendNotificationRequest) throws FirebaseMessagingException {

        return notificationService.SendNotification(sendNotificationRequest);
    }

    @GetMapping("/V1/api/Dashboard/GetAllNotification")
    public List<NotificationMaster> GetAllNotification(){

        return notificationMasterRepo.countSeenStatusForNotificationMasters().stream().map(e->{
             NotificationMaster notificationMaster = (NotificationMaster) e[0];
             Long count=(Long)e[1];
             notificationMaster.setSeenNo(count);
             return notificationMaster;
         }).collect(Collectors.toList());

    }

    @GetMapping("/V1/api/GetNotificationById")
    public List<NotificationMaster> GetNotificationById(@RequestHeader(name = "Authorization") String token){

        return notificationMasterRepo.findFilteredNotificationMasters(Integer.valueOf(tokenService.decodeToken(token.substring(7)).getSubject()));

    }

    @GetMapping("/V1/api/NotSeenNotificationNo")
    public Integer NotSeenNotificationNo(@RequestHeader(name = "Authorization") String token) {
        return notificationService.NotSeenNotificationNo(token);
    }


    @PostMapping("/V1/api/SeenNotification")
    public Boolean SeenNotification(@RequestHeader(name = "Authorization") String token,@RequestBody SeenNotificationRequest seenNotificationRerquest){
        return notificationService.SeenNotification(token,seenNotificationRerquest.notificationId);
    }
    public record SeenNotificationRequest(@JsonProperty("notification_id") Long notificationId){}


}
