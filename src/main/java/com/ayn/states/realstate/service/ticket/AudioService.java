package com.ayn.states.realstate.service.ticket;


import com.ayn.states.realstate.entity.ticket.ImageFile;
import com.ayn.states.realstate.entity.ticket.MessageType;
import com.ayn.states.realstate.exception.UnauthorizedException;
import com.ayn.states.realstate.repository.ticket.ImageFileRepo;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
public class AudioService {

    @Autowired
    Environment environment;
    @Autowired
    ImageFileRepo imageFileRepo;

    @Transactional
    public String uploadAudioToFileSystem(@NotNull MultipartFile file, MessageType contentType) throws IOException {

        String originalFilename = file.getOriginalFilename();
        String newFilename = System.currentTimeMillis() + (originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : null);
        String filePath = environment.getProperty("TICKET_AUDIO_DIR") + newFilename;

        imageFileRepo.save(ImageFile.builder()
                .name(newFilename)
                .type(String.valueOf(contentType))
                .build());

        file.transferTo(new File(filePath));

        return newFilename;
    }

    public byte[] downloadAudioFromFileSystem(String fileName) throws IOException {
        Optional<ImageFile> fileData = imageFileRepo.findFirstByName(fileName);
        String filePath = environment.getProperty("TICKET_AUDIO_DIR") + fileData.orElseThrow(() -> new UnauthorizedException("file not found")).getName();
        return Files.readAllBytes(new File(filePath).toPath());
    }

}
