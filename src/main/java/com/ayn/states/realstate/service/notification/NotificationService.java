package com.ayn.states.realstate.service.notification;

import com.ayn.states.realstate.entity.notification.DTO.AddToken;
import com.ayn.states.realstate.entity.notification.DTO.SendNotificationRequest;
import com.ayn.states.realstate.entity.notification.NotificationDetails;
import com.ayn.states.realstate.entity.notification.NotificationMaster;
import com.ayn.states.realstate.entity.notification.NotificationMessage;
import com.ayn.states.realstate.entity.notification.NotificationToken;
import com.ayn.states.realstate.exception.UnauthorizedException;
import com.ayn.states.realstate.repository.notification.NotificationDetailsRepo;
import com.ayn.states.realstate.repository.notification.NotificationMasterRepo;
import com.ayn.states.realstate.repository.notification.NotificationTokenRepo;
import com.ayn.states.realstate.service.token.TokenService;
import com.google.firebase.messaging.*;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    TokenService tokenService;

    @Autowired
    FirebaseMessaging firebaseMessaging;

    @Autowired
    NotificationMasterRepo notificationMasterRepo;

    @Autowired
    NotificationTokenRepo tokenRepo;

    @Autowired
    NotificationTokenRepo notificationTokenRepo;

    @Autowired
    NotificationDetailsRepo notificationDetailsRepo;

    @Autowired
    JdbcClient jdbcClient;

    @Value("${TOPIC}")
    String topic;


    public String sendNotificationByToken(@NotNull NotificationMessage notificationMessage) {

        Notification notification = Notification
                .builder()
                .setTitle(notificationMessage.getTitle())
                .setBody(notificationMessage.getBody())
                .setImage(notificationMessage.getImage())
                .build();

        Map<String, String> map = new HashMap<>();
        map.put("notification_typ", "3");
        map.put("content_available", "1");

        ApnsConfig apnsConfig = getApnsConfig();
        Message message = Message
                .builder()
                .setToken(notificationMessage.getRecipientToken())
                .setNotification(notification)
                .setApnsConfig(apnsConfig)
                .putAllData(map)
                .build();

        firebaseMessaging.sendAsync(message);
        return "success";
    }


    public NotificationToken AddToken(@NotBlank AddToken addToken, @NotBlank String token) {
        return tokenRepo.save(new NotificationToken(Integer.valueOf(tokenService.decodeToken(token.substring(7)).getSubject()), addToken.token(), addToken.token_type()));

    }


    public NotificationMaster SendNotification(SendNotificationRequest sendNotificationRequest) throws FirebaseMessagingException {
        switch (sendNotificationRequest.notificationType()) {
            case NORMAL_USER -> {
                List<String> tokens = new ArrayList<>();
                Optional<String> tokenTemp;
                List<NotificationDetails> notificationDetailsList = sendNotificationRequest.notificationDetails().stream().toList();
                for (int i = 0; i < sendNotificationRequest.notificationDetails().size(); i++) {
                    tokenTemp = notificationTokenRepo.findTokenByID(notificationDetailsList.get(i).getSendTo());
                    if (tokenTemp.isPresent()) {
                        tokens.add(tokenTemp.get());
                    } else throw new UnauthorizedException("token not found");
                }

                if (!tokens.isEmpty()) {

                    var NotificationMasterfinal = notificationMasterRepo.save(new NotificationMaster(
                            changeNumberToEnglish(sendNotificationRequest.Title()), changeNumberToEnglish(sendNotificationRequest.Body()), new Date(),
                            new Date(), 0, 0, true, sendNotificationRequest.notificationType(),
                            sendNotificationRequest.notificationDetails()
                    ));
                    //notificationDetailsRepo.saveAll(sendNotificationRequest.notificationDetails());

                    sendNotificationRequest.notificationDetails().forEach(u->{
                        NotificationMasterfinal.getNotificationDetails().add(new NotificationDetails(u.getSendTo(),NotificationMasterfinal));
                        notificationDetailsRepo.save(new NotificationDetails(u.getSendTo(),NotificationMasterfinal));
                    });
                    notificationMasterRepo.save(NotificationMasterfinal);
                    Notification notification = Notification
                            .builder()
                            .setTitle(sendNotificationRequest.Title())
                            .setBody(sendNotificationRequest.Body())
                            .build();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                    formatter.setTimeZone(TimeZone.getTimeZone("Asia/Baghdad"));
                    String createAtFormatted = formatter.format(NotificationMasterfinal.getCreateAt());

                    List<Message> messageList = new ArrayList<>();
                    Map<String, String> map = new HashMap<>();
                    map.put("id", String.valueOf(NotificationMasterfinal.getNotificationId()));
                    map.put("body", NotificationMasterfinal.getBody());
                    map.put("title", NotificationMasterfinal.getTitle());
                    map.put("create_at", createAtFormatted);
                    map.put("notification_typ", "3");
                    map.put("content_available", "1");

                    for (String t : tokens) {
                        ApnsConfig apnsConfig = getApnsConfig();

                        Message message = Message.builder()
                                .setToken(t)
                                .setNotification(notification)
                                .setApnsConfig(apnsConfig)
                                .putAllData(map)
                                .build();
                        messageList.add(message);
                    }

                    firebaseMessaging.sendEachAsync(messageList);
                    return NotificationMasterfinal;
                } else throw new UnauthorizedException("no token available");

            }
            case ALL -> {
                var NotificationMasterfinal = notificationMasterRepo.save(new NotificationMaster(
                        changeNumberToEnglish(sendNotificationRequest.Title()), changeNumberToEnglish(sendNotificationRequest.Body()), new Date(),
                        new Date(), 0, 0, true, sendNotificationRequest.notificationType(),
                        sendNotificationRequest.notificationDetails()
                ));
//                tmEcDelegateRepository.findAll().forEach(u->{
//                    NotificationMasterfinal.getNotificationDetails().add(new NotificationDetails(u.getDelegateId(),NotificationMasterfinal));
//                    notificationDetailsRepo.save(new NotificationDetails(u.getDelegateId(),NotificationMasterfinal));
//                });
                notificationMasterRepo.save(NotificationMasterfinal);

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                formatter.setTimeZone(TimeZone.getTimeZone("Asia/Baghdad"));
                String createAtFormatted = formatter.format(NotificationMasterfinal.getCreateAt());
                Map<String, String> map = new HashMap<>();
                map.put("id", String.valueOf(NotificationMasterfinal.getNotificationId()));
                map.put("body", NotificationMasterfinal.getBody());
                map.put("title", NotificationMasterfinal.getTitle());
                map.put("create_at", createAtFormatted);
                map.put("notification_typ", "3");
                map.put("content_available", "1");
                Notification notification = Notification
                        .builder()
                        .setTitle(sendNotificationRequest.Title())
                        .setBody(sendNotificationRequest.Body())
                        .build();

                Message message = Message.builder()
                        .setNotification(notification)
                        .setTopic(topic)
                        .putAllData(map)
                        .build();

                firebaseMessaging.sendAsync(message);
            }

        }

        return null;
    }


    private ApnsConfig getApnsConfig() {
        Map<String, Object> map2 = new HashMap<>();
        map2.put("content_available", 1);
        ApsAlert apsAlert = ApsAlert.builder().setTitle("AL-AYN").build();
        return ApnsConfig.builder()
                .setAps(Aps.builder().setSound("1").putAllCustomData(map2).setAlert(apsAlert).build()).build();
    }

    String changeNumberToEnglish(String content) {
        String arabicDigits = "٠١٢٣٤٥٦٧٨٩";
        content = content.chars()
                .mapToObj(c -> {
                    if (arabicDigits.indexOf(c) >= 0) {
                        return (char) (c - '٠' + '0');
                    } else {
                        return (char) c;
                    }
                })
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        return content;
    }


    public Integer NotSeenNotificationNo(String token) {

        return notificationDetailsRepo.findCount(Long.valueOf(tokenService.decodeToken(token.substring(7)).getSubject()));

    }


    public Boolean SeenNotification(String token, Long notificationId) {


        Long updateId = Long.valueOf(tokenService.decodeToken(token.substring(7)).getSubject());
        try {
            jdbcClient.sql("""
                                UPDATE ST_APP_notification_token
                                SET notification_Un_Read_No =0 WHERE userId IN (:userId)
                            """).param("userId", updateId)
                    .update();

            return true;
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }

    }


}
