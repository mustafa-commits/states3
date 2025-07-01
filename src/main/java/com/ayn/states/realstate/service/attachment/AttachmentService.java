package com.ayn.states.realstate.service.attachment;


import com.ayn.states.realstate.entity.att.Attachments;
import com.ayn.states.realstate.repository.attachment.AttachmentsRepo;
import jakarta.persistence.Cacheable;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentsRepo attachmentsRepo;

    @Value("${FOLDER_PATH}")
    private String basePath;

    public ResponseEntity<?> getStateAttachment(String fileName) {
        if (attachmentsRepo.getAttachmentsByUrlImageExists(fileName)) {
            try {
                String fileExtension = FilenameUtils.getExtension(fileName);
                MediaType contentType = MediaType.APPLICATION_JSON;
                if (fileExtension.equalsIgnoreCase("pdf")) {
                    contentType = MediaType.APPLICATION_PDF;
                } else if (fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpeg")) {
                    contentType = MediaType.valueOf("image/jpeg");
                } else if (fileExtension.equalsIgnoreCase("png")) {
                    contentType = MediaType.valueOf("image/png");
                }
                // Build the response with file content
                return ResponseEntity.ok()
                        .contentType(contentType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .body(Files.readAllBytes(new File(basePath + fileName).toPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return ResponseEntity.notFound().build();
    }
}
