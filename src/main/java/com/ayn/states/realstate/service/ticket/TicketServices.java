package com.ayn.states.realstate.service.ticket;

import com.ayn.states.realstate.entity.notification.NotificationToken;
import com.ayn.states.realstate.entity.notification.TokenType;
import com.ayn.states.realstate.entity.ticket.DTO.ArchiveChatRequest;
import com.ayn.states.realstate.entity.ticket.DTO.DeleteMessage;
import com.ayn.states.realstate.entity.ticket.DTO.TicketDto;
import com.ayn.states.realstate.entity.ticket.DTO.TicketType;
import com.ayn.states.realstate.entity.ticket.MessageType;
import com.ayn.states.realstate.entity.ticket.Ticket;
import com.ayn.states.realstate.entity.ticket.TicketDetails;
import com.ayn.states.realstate.exception.UnauthorizedException;
import com.ayn.states.realstate.repository.notification.NotificationTokenRepo;
import com.ayn.states.realstate.repository.ticket.TicketDetailsRepo;
import com.ayn.states.realstate.repository.ticket.TicketRepo;
import com.ayn.states.realstate.repository.user.UsersRepo;
import com.google.firebase.messaging.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

//@Slf4j
@Service

public class TicketServices {


    private final TicketDetailsRepo ticketDetailsRepo;

    private final TicketRepo ticketRepo;

    @Autowired
    public TicketServices(TicketDetailsRepo ticketDetailsRepo, TicketRepo ticketRepo) {
        this.ticketDetailsRepo = ticketDetailsRepo;
        this.ticketRepo = ticketRepo;
    }

    @Autowired
    private FirebaseMessaging firebaseMessaging;


    @Autowired
    private NotificationTokenRepo notificationTokenRepo;

    @Autowired
    private UsersRepo usersRepo;


    @NotNull
    private static TicketDetails getTicketDetails(Object[] all) {
        TicketDetails ticketDetails = (TicketDetails) all[1];
        if (ticketDetails.getMessageType() == MessageType.IMAGE.ordinal()) {
            ticketDetails.setContent("تم ارسال صورة");
        } else if (ticketDetails.getMessageType() == MessageType.PDF.ordinal()) {
            ticketDetails.setContent("تم ارسال pdf");
        } else if (ticketDetails.getMessageType() == MessageType.M4A.ordinal()) {
            ticketDetails.setContent("تم ارسال صوت");
        }
        return ticketDetails;
    }

    public Ticket createTicket(String title, int user_id, String content) {
        title = changeNumberToEnglish(title);
        content = changeNumberToEnglish(content);

        try {
            Ticket t1 = ticketRepo.save(new Ticket(title, user_id, new Date(), new Date(), user_id, true, user_id, '0', TicketType.FromUser));

            TicketDetails td = ticketDetailsRepo.save(new TicketDetails(content, t1.getId(), MessageType.TEXT, user_id, 0, new Date(), user_id));

            sendNotification(0, user_id, content, td, t1, 100);

//            createDetails(t1.getId(),
//                    "السلام عليكم ورحمة الله وبركاته...\n" +
//                    "نود أن نلفت عنايتكم ألى ان كادر الدعم الفني متواجدين للإجابة على استفساراتكم من الساعة ( 8:00 ) صباحاً لغاية الساعة ( 5:00 ) مساءً  طيلة أيام الأسبوع باستثناء يوم الجمعة,لطفاً يرجى ارسال استفسارك بالتفصيل ليتسنى لنا الإجابة على طلبكم بأسرع وقت ممكن , \n" +
//                    "مع الشكر والتقدير .", user_id, 0L, MessageType.TEXT);
            return t1;
        } catch (Exception e) {
         //   log.error(e.getMessage());
            throw new UnauthorizedException("incorrect token");
        }
    }

    public List<TicketDetails> getDetails(int ticketId, int page) {

        return ticketDetailsRepo.findAllByTicketIdOrderByIdDesc(ticketId, PageRequest.of((page > 0) ? page - 1 : 0, 100));

    }

    @Transactional
    public TicketDetails getDetailsById(int ticketDetailsId) {
        return ticketDetailsRepo.findById(ticketDetailsId).orElseThrow();
    }

    @Transactional
    public TicketDetails createDetails(int ticketId, String content, int receiver, int sender, MessageType messageType) {
        Ticket t1 = ticketRepo.findById(ticketId).orElseThrow(() -> new UnauthorizedException("Ticket not found"));
        if (t1.getIsActive().equals(false))
            throw new UnauthorizedException("ticket is closed");
        t1.setUpdateAt(new Date());
        t1.setModifiedUser(sender);
        ticketRepo.save(t1);// receiver 0
        content = changeNumberToEnglish(content);

        var ticketdetailsvar = ticketDetailsRepo.save(new TicketDetails(content, ticketId, messageType, sender, receiver, new Date(), sender,null));

        sendNotification(receiver, sender, content, ticketdetailsvar, t1, 0);
        return ticketdetailsvar;

    }

    @Async(value = "asyncExecutor")
    public void sendNotification(int receiver, int sender, String content, TicketDetails details, Ticket ticket, int flag) {

        new Thread(() -> {

            String body;


            if (flag == 100) {
                String name;

                String fullnameReciver = usersRepo.findNameById(receiver);
                if (fullnameReciver != null) {
                    name = "";
                    String[] chars = fullnameReciver.split(" ");
                    if (chars.length >= 3) {
                        name = chars[0] + " " + chars[1] + " " + chars[2];
                    } else if (chars.length == 2) {
                        name = chars[0] + " " + chars[1];
                    } else if (chars.length == 1) {
                        name = chars[0];
                    }
                } else {
                    name = "اشعار";
                }
                if (details.getMessageType() == MessageType.IMAGE.ordinal()) {
                    body = "تم ارسال صورة";
                } else if (details.getMessageType() == MessageType.PDF.ordinal()) {
                    body = "تم ارسال pdf";
                } else
                    body = content;
                Notification notification = Notification
                        .builder()
                        .setTitle(name) //spo name
                        .setBody(body)
                        .build();

                List<String> tokens = new ArrayList<>();
                List<Integer> ids = new ArrayList<>();
                List<NotificationToken> notificationTokens = notificationTokenRepo.findAllByTokenType(TokenType.DASHBOARD);

                for (int i = 0; i < notificationTokens.size(); i++) {
                    tokens.add(notificationTokens.get(i).getToken());
                    ids.add(notificationTokens.get(i).getUserId());
                }

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                formatter.setTimeZone(TimeZone.getTimeZone("Asia/Baghdad"));
                String createAtFormatted = formatter.format(ticket.getCreatedAt());
                String modifyAtFormatted = formatter.format(details.getModifiedAt());


                if (tokens.size() > 0) {
                    List<Message> messageList = new ArrayList<>();
                    for (int i = 0; i < tokens.size(); i++) {

                        Map<String, String> map = new HashMap<>(details.toMap());
                        map.put("modify_at", modifyAtFormatted);
                        map.put("beneficent_no", String.valueOf(ids.get(i)));
                        map.put("create_user", String.valueOf(ticket.getCreatedUser()));
                        map.put("sponsor_name", name);
                        map.put("sponsor_full_name", Optional.ofNullable(fullnameReciver).orElse(""));
                        map.put("create_at", createAtFormatted);
                        map.putAll(ticket.toMap());
                        map.put("send_or_receive", String.valueOf(0));
                        map.put("notification_typ", "0");
                        map.put("content_available", "1");

                        ApnsConfig apnsConfig = getApnsConfig();

                        Message message = Message.builder()
                                .setToken(tokens.get(i))
                                .setNotification(notification)
                                .setApnsConfig(apnsConfig)
                                .putAllData(
                                        map
                                )
                                .build();
                        messageList.add(message);
                    }

                    firebaseMessaging.sendEachAsync(messageList);
                }
            } else if (receiver == 0) {  //web

                String fullnameReciver = usersRepo.findNameById(sender);
                String name = "";
                String[] chars = fullnameReciver.split(" ");
                if (chars.length >= 3) {
                    name = chars[0] + " " + chars[1] + " " + chars[2];
                } else if (chars.length == 2) {
                    name = chars[0] + " " + chars[1];
                } else if (chars.length == 1) {
                    name = chars[0];
                }

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                formatter.setTimeZone(TimeZone.getTimeZone("Asia/Baghdad"));
                String createAtFormatted = formatter.format(ticket.getCreatedAt());
                String modifyAtFormatted = formatter.format(details.getModifiedAt());

                // 0 create ticket
                // 1 send from app
                // 2 send from web
                // 3 other notification

                if (details.getMessageType() == MessageType.IMAGE.ordinal()) {
                    body = "تم ارسال صورة";
                } else if (details.getMessageType() == MessageType.PDF.ordinal()) {
                    body = "تم ارسال pdf";
                } else
                    body = content;

                Notification notification = Notification
                        .builder()
                        .setTitle(name) //spo name
                        .setBody(body)
                        .build();

                List<String> tokens = new ArrayList<>();
                List<Integer> ids = new ArrayList<>();
                List<NotificationToken> notificationTokens = notificationTokenRepo.findAllByTokenType(TokenType.DASHBOARD);

                for (NotificationToken notificationToken : notificationTokens) {
                    tokens.add(notificationToken.getToken());
                    ids.add(notificationToken.getUserId());
                }
                List<Message> messageList = new ArrayList<>();
                if (!tokens.isEmpty()) {

                    for (int i = 0; i < tokens.size(); i++) {
                        Map<String, String> map = new HashMap<>(details.toMap());
                        map.put("modify_at", modifyAtFormatted);
                        map.put("beneficent_no", String.valueOf(ids.get(i)));
                        map.put("create_user", String.valueOf(ticket.getCreatedUser()));
                        map.put("sponsor_name", name);
                        map.put("sponsor_full_name", fullnameReciver);
                        map.put("create_at", createAtFormatted);
                        map.putAll(ticket.toMap());
                        map.put("send_or_receive", String.valueOf(0));
                        map.put("notification_typ", "1");
                        map.put("content_available", "1");

                        ApnsConfig apnsConfig = getApnsConfig();
                        Message message = Message.builder()
                                .setToken(tokens.get(i))
                                .setNotification(notification)
                                .setApnsConfig(apnsConfig)
                                .putAllData(
                                        map
                                )
                                .build();
                        messageList.add(message);
                    }

                    firebaseMessaging.sendEachAsync(messageList);

                }

            } else if (receiver != 0) {

                String fullnameReciver = usersRepo.findNameById(receiver);
                String name = "";
                String[] chars = fullnameReciver.split(" ");
                if (chars.length >= 3) {
                    name = chars[0] + " " + chars[1] + " " + chars[2];
                } else if (chars.length == 2) {
                    name = chars[0] + " " + chars[1];
                } else if (chars.length == 1) {
                    name = chars[0];
                }
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                formatter.setTimeZone(TimeZone.getTimeZone("Asia/Baghdad"));
                String createAtFormatted = formatter.format(ticket.getCreatedAt());
                Map<String, String> map = new HashMap<>(details.toMap());
                map.put("modify_at", details.getModifiedAt().toString());
                map.put("beneficent_no", String.valueOf(notificationTokenRepo.findById(receiver).get().getUserId()));
                map.put("create_user", String.valueOf(ticket.getCreatedUser()));
                map.put("sponsor_name", name);
                map.put("sponsor_full_name", fullnameReciver);
                map.put("create_at", createAtFormatted);
                map.putAll(ticket.toMap());
                map.put("send_or_receive", String.valueOf(0));
                map.put("notification_typ", "2");
                map.put("content_available", "1");

                if (details.getMessageType() == MessageType.IMAGE.ordinal()) {
                    body = "تم ارسال صورة";
                } else if (details.getMessageType() == MessageType.PDF.ordinal()) {
                    body = "تم ارسال pdf";
                } else
                    body = content;

                Notification notification = Notification
                        .builder()
                        .setTitle(ticket.getTitle())
                        .setBody(body)
                        .build();


                String token;


                if (notificationTokenRepo.findById(receiver).isPresent()) {
                    token = notificationTokenRepo.findById(receiver).get().getToken();


                    ApnsConfig apnsConfig = getApnsConfig();
                    Message message = Message
                            .builder()
                            .setToken(token)
                            .putAllData(
                                    map
                            ).setApnsConfig(apnsConfig)
                            .setNotification(notification)
                            .build();
                    firebaseMessaging.sendAsync(message);
                }
            }
        }).start();
    }

    private ApnsConfig getApnsConfig() {
        Map<String, Object> map2 = new HashMap<>();
        map2.put("content_available", 1);
        ApsAlert apsAlert = ApsAlert.builder().setTitle("AL-AYN").build();
        return ApnsConfig.builder().
                setAps(Aps.builder().setAlert(apsAlert).setCategory("اشعار")//.setBadge(5)
                        .setContentAvailable(true).setMutableContent(true)
                        .setSound("1").putAllCustomData(map2).setBadge(1)
                        .build()).build();
    }

    public List<TicketDto> getAllTicketsWithLastDetails(int id) {

        return ticketRepo.findAllByuserIdquery(id).stream()
                .map(all -> {
                    Ticket ticket = (Ticket) all[0];
                    TicketDetails ticketDetails = getTicketDetails(all);

                    return new TicketDto(ticket, ticketDetails, 0, 0, ticket.getUpdateAt(), ticket.getCreatedUser(), "", "", ticket.getCreatedAt());
                })
                .collect(Collectors.toList());
    }

    public List<TicketDto> getAllTickets() {

        return ticketRepo.findAllTicketsWithLastDetails().stream()
                .map(all -> {
                    Ticket ticket = (Ticket) all[0];
                    TicketDetails ticketDetails = getTicketDetails(all);

                    String fullname = (String) all[2];
                    if (fullname != null) {
                        String[] chars = fullname.split(" ");
                        String name = Arrays.stream(chars).limit(3).collect(Collectors.joining(" "));
                        return new TicketDto(ticket, ticketDetails, ticketDetailsRepo.findAllNotSeenWithTicket(ticket.getId()), 0, ticket.getUpdateAt(), ticket.getCreatedUser(), name, fullname, ticket.getCreatedAt());
                    } else {
                        return new TicketDto(ticket, ticketDetails, ticketDetailsRepo.findAllNotSeenWithTicket(ticket.getId()), 0, ticket.getUpdateAt(), ticket.getCreatedUser(), "name", fullname, ticket.getCreatedAt());

                    }
                })
                .collect(Collectors.toList());

    }

    public Boolean closeTicket(String id, ArchiveChatRequest archiveChatRequest) {
        Ticket ticket;
        if (ticketRepo.findById(archiveChatRequest.chatId()).isPresent()) {
            ticket = ticketRepo.findById(archiveChatRequest.chatId()).get();
            ticket.setIsActive(false);
            ticket.setModifiedUser(Integer.parseInt(id));
            ticket.setUpdateAt(new Date());
            ticketRepo.save(ticket);
            return true;
        } else {
            return false;

        }
    }

    public int setReminder(int ticketId, int userId) {
        Ticket ticket = ticketRepo.findById(ticketId).orElseThrow();
        if (ticket.getReminder() == '0') {
            ticket.setReminder('1');
            ticket.setModifiedUser(userId);
            ticket.setUpdateAt(new Date());
            ticketRepo.save(ticket);
            return ticket.getReminder();
        } else if (ticket.getReminder() == '1') {
            ticket.setReminder('0');
            ticket.setModifiedUser(userId);
            ticket.setUpdateAt(new Date());
            ticketRepo.save(ticket);
            return ticket.getReminder();
        }

        return 0;
    }

    public Boolean openChat(String id, ArchiveChatRequest getClosedTicket) {
        Ticket ticket;
        if (ticketRepo.findById(getClosedTicket.chatId()).isPresent()) {
            ticket = ticketRepo.findById(getClosedTicket.chatId()).get();
            ticket.setIsActive(true);
            ticket.setModifiedUser(Integer.parseInt(id));
            ticket.setUpdateAt(new Date());
            ticketRepo.save(ticket);
            return true;
        } else {
            return false;

        }


    }

    public boolean DeleteDetails(String id, DeleteMessage deleteMessage) {
        TicketDetails details;
        if (ticketDetailsRepo.findById(deleteMessage.messageId()).isPresent()) {
            details = ticketDetailsRepo.findById(deleteMessage.messageId()).get();
            details.setIsActive(false);
            details.setModifiedUser(Integer.parseInt(id));
            details.setModifiedAt(new Date());
            ticketDetailsRepo.save(details);
            return true;
        } else {
            return false;

        }


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


    public Integer getAllTicketsNotSeen() {
        return Optional.of(ticketDetailsRepo.findAllNotSeen().size())
                .orElse(0);
    }


    public Integer countTicketsNotSeen(int token) {
        return ticketDetailsRepo.findAllNotSeenUser(token);

    }


    public TicketDetails createreplayDetails(int ticketId, String content, int receiver, int sender, MessageType messageType, int referenceDetailsId, String referenceMessage) {

        var td= ticketDetailsRepo.save(new TicketDetails(changeNumberToEnglish(content), ticketId, messageType, sender, receiver, new Date(), sender,referenceDetailsId, referenceMessage));
        new Thread(() -> {
        sendNotification(receiver, sender, changeNumberToEnglish(content), td, ticketRepo.findById(ticketId).orElseThrow(), 0);
        }).start();
       return td;


    }






}