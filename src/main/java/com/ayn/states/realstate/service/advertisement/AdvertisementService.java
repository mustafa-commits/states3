package com.ayn.states.realstate.service.advertisement;

import com.ayn.states.realstate.entity.advertisement.Advertisement;
import com.ayn.states.realstate.entity.advertisement.AdvertisementType;
import com.ayn.states.realstate.repository.advertisement.AdvertisementRepository;
import com.ayn.states.realstate.service.msg.WhatsAppService;
import com.ayn.states.realstate.service.token.TokenService;
import com.tinify.Source;
import com.tinify.Tinify;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
public class AdvertisementService {

    @Autowired
    private AdvertisementRepository repo;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private WhatsAppService whatsAppService;

    @Value("${AD_DIR}")
    private File basePath;

    public List<Advertisement> getActiveAds() {
        return repo.findByIsActiveTrue()
                .stream()
                .filter(Advertisement::isStillValid)
                .toList();
    }

    public Advertisement saveAd(String title, Integer targetId, AdvertisementType type, MultipartFile image, boolean isActive, String token, int period, Long advertiserPhone, String body) throws IOException {

        String newfileNames=null;
        if (!image.isEmpty()) {
            String originalFilename = image.getOriginalFilename();
            newfileNames=System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));
            image.transferTo(new File(basePath + newfileNames));
            Tinify.setKey("28MYcgnnzHSNYkgQLt17tNMn80RnHk2c");
            Source source = Tinify.fromFile(new File(basePath + newfileNames).getPath());
            source.toFile(basePath + newfileNames);
        }
        if (AdvertisementType.TEXT.equals(type)) {
            whatsAppService.sendMessage("+964"+advertiserPhone, String.format(
                    """
                    تمت اضافه الاعلان الخاص بكم على تطبيق زون
                    الاعلان صالح لمـدة %s يوم
                    """,
                    period
            ));
        }
        return repo.save(
                new Advertisement(title,newfileNames,targetId,type,isActive,Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject())
                        ,period,advertiserPhone,body)
        );
    }

    public ResponseEntity<?> getAttachment(String fileName) {
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


}

