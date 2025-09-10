package com.ayn.states.realstate.service.ticket;


import com.ayn.states.realstate.entity.ticket.ImageFile;
import com.ayn.states.realstate.entity.ticket.MessageType;
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
public class ImageService {

    @Autowired
    Environment environment;
    @Autowired
    ImageFileRepo imageFileRepo;

    @Transactional
    public String uploadImageToFileSystem(@NotNull MultipartFile file, MessageType contentType) throws IOException {

        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String newFilename = System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));
        String filePath = environment.getProperty("TICKET_ATT_DIR") + newFilename;
        imageFileRepo.save(ImageFile.builder()
                .name(newFilename)
                .type(String.valueOf(contentType))
                .build());
        file.transferTo(new File(filePath));

        return newFilename;
    }

    public byte[] downloadFileFromFileSystem(String fileName) throws IOException {
        Optional<ImageFile> fileData = imageFileRepo.findFirstByName(fileName);
        if (fileData.isPresent()) {
            String filePath = environment.getProperty("TICKET_ATT_DIR") + fileData.get().getName();
            return Files.readAllBytes(new File(filePath).toPath());
        } else
            return null;
    }

    public byte[] downloadFileFromFileSystemTemp() throws IOException {
        String filePath = environment.getProperty("TICKET_ATT_DIR") + "home.mp4";
        return Files.readAllBytes(new File(filePath).toPath());

    }
}
