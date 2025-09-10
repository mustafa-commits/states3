package com.ayn.states.realstate.controller;


import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.entity.ticket.DTO.*;
import com.ayn.states.realstate.entity.ticket.MessageType;
import com.ayn.states.realstate.entity.ticket.Ticket;
import com.ayn.states.realstate.entity.ticket.TicketDetails;
import com.ayn.states.realstate.repository.ticket.TicketDetailsRepo;
import com.ayn.states.realstate.repository.ticket.TicketRepo;
import com.ayn.states.realstate.service.ticket.AudioService;
import com.ayn.states.realstate.service.ticket.ImageService;
import com.ayn.states.realstate.service.ticket.TicketServices;
import com.ayn.states.realstate.service.token.TokenService;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;


@RestController
@Slf4j
public class TicketController implements SecuredRestController {


    @Autowired
    private TicketServices ticketServices;


    @Autowired
    private TokenService tokenService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private TicketDetailsRepo ticketDetailsRepo;


    @Autowired
    private Environment environment;

    private String imagePath;
    private String audioPath;

    @Autowired
    private AudioService audioService;

    @PostConstruct
    public void init() {
        imagePath = environment.getProperty("TICKET_ATT_URL");
        audioPath = environment.getProperty("TICKET_AUDIO_URL");
    }

    record CreateTicket(String title, int user_id, String content){}

    @PostMapping("/V1/api/createTicket")
    public Ticket createTicket(@RequestHeader(name = "Authorization") String token,
                               @RequestBody CreateTicket createTicket){
        return ticketServices.createTicket(createTicket.title(),
                Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject()),createTicket.content());
    }

    @PostMapping("/V1/api/GetMessagesByChatId")
    public List<TicketDetails> getDetails(@RequestHeader(name = "Authorization") String token, @Valid @RequestBody GetMessagesRequest getDetailsRequest){

        List<TicketDetails> allTicketsdetails=ticketServices.getDetails(getDetailsRequest.chatId(), getDetailsRequest.page());
        for (TicketDetails ticketDto: allTicketsdetails){
            if(ticketDto.getMessageType()== MessageType.M4A.ordinal())
                ticketDto.setContent(audioPath+ticketDto.getContent());
            else if(ticketDto.getMessageType()!=0)
                ticketDto.setContent(imagePath+ticketDto.getContent());
        }
        new Thread(() -> {

            if("dashboard".equals(tokenService.decodeToken(token.substring(7)).getClaims().get("scope")))
                ticketDetailsRepo.setTicketAsSeen(allTicketsdetails.get(0).getTicketId(), 0);
                // ticketDetailsRepo.setTicketAsSeenMobile(allTicketsdetails.get(0).getTicketId());
            else if ("zoneUser".equals(tokenService.decodeToken(token.substring(7)).getClaims().get("scope"))) {
                //   ticketDetailsRepo.setTicketAsSeen(allTicketsdetails.get(0).getTicketId(), 0);
                ticketDetailsRepo.setTicketAsSeenMobile(allTicketsdetails.get(0).getTicketId());

            }

        }).start();
        return allTicketsdetails;
    }

//    @PostMapping("/V1/api/getdetailsById/dashboard")
//    List<TicketDetails> getDetailsDashboard(@RequestHeader(name = "Authorization") String token, @NotNull @RequestBody GetDetailsRequest getDetailsRequest){
//
//        List<TicketDetails> allTicketsdetails=ticketServices.getDetails(getDetailsRequest.ticketId(), getDetailsRequest.page());
//        for (TicketDetails ticketDto: allTicketsdetails){
//            if(ticketDto.getMessageType()==MessageType.M4A.ordinal())
//                ticketDto.setContent(audioPath+ticketDto.getContent());
//            else if(ticketDto.getMessageType()!=0)
//                ticketDto.setContent(imagePath+ticketDto.getContent());
//        }
//        new Thread(() -> {
//
//            ticketDetailsRepo.setTicketAsSeen(allTicketsdetails.get(0).getTicketId(), 0);
//
//        }).start();
//        return allTicketsdetails;
//    }
    public record CreateReplayRequest(@NotBlank String content, int receiver, @JsonProperty("message_id") int messageId, @JsonProperty("reference_message_id") int referenceMessageId, @NotBlank @JsonProperty("reference_message") String referenceMessage) {
    }
    @PostMapping("/V1/api/addReplay")
    public TicketDetails addReplay(@RequestHeader(name = "Authorization") String token, @Valid @RequestBody CreateReplayRequest createReplayRequest) {

        return ticketServices.createreplayDetails(
                createReplayRequest.messageId(),
                createReplayRequest.content(),
                createReplayRequest.receiver(), Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject()), MessageType.TEXT,
                createReplayRequest.referenceMessageId(),
                createReplayRequest.referenceMessage());
    }
//    @PostMapping("/V1/api/getdetailsBydetailsId")
//    TicketDetails getdetailsBydetailsId(@NotNull @RequestBody GetDetailsRequestById getDetailsRequestById){
//
//        TicketDetails Ticketsdetails=ticketServices.getDetailsById(getDetailsRequestById.details_id());
//        if(Ticketsdetails.getMessageType()==MessageType.M4A.ordinal())
//            Ticketsdetails.setContent(audioPath+Ticketsdetails.getContent());
//        else if(Ticketsdetails.getMessageType()!=0)
//            Ticketsdetails.setContent(imagePath+Ticketsdetails.getContent());
//
//
//        return Ticketsdetails;
//
//    }

    @GetMapping("/V1/api/app/AllChats")
    public List<TicketDto> getAllTicketsById(@RequestHeader(name = "Authorization") String token){

        return ticketServices.getAllTicketsWithLastDetails(Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject()));
    }

    @GetMapping("/V1/api/Dashboard/AllChats")
    public List<TicketDto>  getAllTickets(){   //synchronized

        return ticketServices.getAllTickets();
    }

    @GetMapping("/V1/api/Dashboard/CountMessagesNotSeen")
    public int getAllTicketsNotSeen(){
        return ticketServices.getAllTicketsNotSeen();
    }

    @GetMapping("/V1/api/app/CountMessagesNotSeen")
    public Integer getAllTicketsNotSeenUser(@RequestHeader(name = "Authorization") String token){
        return ticketServices.countTicketsNotSeen(Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject()));
    }

    @PostMapping("/V1/api/SendMessage")
    public TicketDetails addDetails(@RequestHeader(name = "Authorization") String token, @Valid @RequestBody CreateDetailsRequest createDetailsRequest) {
        return ticketServices.createDetails(createDetailsRequest.ticket_id(),
                createDetailsRequest.content(),
                createDetailsRequest.receiver(), Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject()),MessageType.TEXT);
    }




    @PostMapping("/V1/api/uploadImg")
    public TicketDetails uploadImageToFIleSystem(@RequestHeader(name = "Authorization") String token, @NotNull @RequestParam("image") MultipartFile file,
                                                 @RequestParam("ticket_id") int ticket_id,
                                                 @RequestParam("receiver") int receiver) throws IOException{
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        MessageType contentType;
        if (fileExtension.equalsIgnoreCase("pdf")) {
            contentType = MessageType.PDF;
        } else if (fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpeg")) {
            contentType = MessageType.IMAGE;
        } else if (fileExtension.equalsIgnoreCase("png")) {
            contentType = MessageType.IMAGE;
        } else {
            // Handle unsupported file types
            return null;
        }
        return ticketServices.createDetails(ticket_id,
                imagePath+imageService.uploadImageToFileSystem(file,contentType),
                receiver, Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject()),contentType);
    }


    @GetMapping("/V1/api/downloadImg/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) throws IOException {
        String fileExtension = FilenameUtils.getExtension(fileName);
        MediaType contentType;

        if (fileExtension.equalsIgnoreCase("pdf")) {
            contentType = MediaType.APPLICATION_PDF;
        } else if (fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpeg")) {
            contentType = MediaType.valueOf("image/png");
        } else if (fileExtension.equalsIgnoreCase("png")) {
            contentType = MediaType.valueOf("image/png");
        } else {
            // Handle unsupported file types
            return ResponseEntity.badRequest()
                    .body("Unsupported file type: " + fileExtension);
        }

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(imageService.downloadFileFromFileSystem(fileName));
    }



    @PostMapping("/V1/api/ArchiveChat")
    public boolean closeTicket(@RequestHeader(name = "Authorization") String token, @RequestBody ArchiveChatRequest archiveChatRequest){

        return ticketServices.closeTicket(tokenService.decodeToken(token.substring(7)).getSubject(),archiveChatRequest);
    }

    @PostMapping("/V1/api/OpenChat")
    public boolean OpenTicket(@RequestHeader(name = "Authorization") String token, @RequestBody ArchiveChatRequest archiveChatRequest){

        return ticketServices.openChat(tokenService.decodeToken(token.substring(7)).getSubject(),archiveChatRequest);
    }

    @PostMapping("/V1/api/DeleteMessage")
    public boolean DeleteDetails(@RequestHeader(name = "Authorization") String token, @RequestBody DeleteMessage deleteMessage){

        return ticketServices.DeleteDetails(tokenService.decodeToken(token.substring(7)).getSubject(),deleteMessage);
    }


    @PostMapping("/V1/api/setReminder")
    public int Reminder(@RequestHeader(name = "Authorization") String token, @NotNull @RequestBody ReminderRecord reminderRecord){

        return ticketServices.setReminder(reminderRecord.chatId(), Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject()));
    }


    @PostMapping("/V1/api/uploadAudio")
    public TicketDetails uploadImageToFIleSystem(@RequestHeader(name = "Authorization", required = true) String token, @RequestParam("audio") MultipartFile file,
                                                 @RequestParam("ticket_id") int ticket_id,
                                                 @RequestParam("receiver") Integer receiver) throws IOException, ExecutionException, InterruptedException {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        MessageType contentType;
        if (fileExtension.equalsIgnoreCase("m4a")) {
            contentType = MessageType.M4A;
        }else {
            // Handle unsupported file types
            return null;
        }
        String uploadAudio = audioService.uploadAudioToFileSystem(file,contentType);
        TicketDetails td=ticketServices.createDetails(ticket_id,
                uploadAudio,
                receiver, Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject().toString()),contentType);
        td.setContent(audioPath+uploadAudio);
        return td;
  /*  return ResponseEntity.status(HttpStatus.OK)
                .body(uploadImage);*/
    }

    @GetMapping("/V1/api/stream-audio/{fileName}")
    public ResponseEntity<ByteArrayResource> streamAudio(@PathVariable String fileName) throws IOException {
        byte[] fileData = audioService.downloadAudioFromFileSystem(fileName);

        ByteArrayResource resource = new ByteArrayResource(fileData);

        // Set the content type as audio/mpeg
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));

        // Return the resource as a stream
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }




}

