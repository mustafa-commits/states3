package com.ayn.states.realstate.service.compound;

import com.ayn.states.realstate.controller.RealStatesController;
import com.ayn.states.realstate.dto.compound.*;
import com.ayn.states.realstate.entity.compound.Compound;
import com.ayn.states.realstate.entity.compound.CompoundPost;
import com.ayn.states.realstate.entity.compound.SocialLinks;
import com.ayn.states.realstate.entity.compound.UnitMap;
import com.ayn.states.realstate.entity.notification.NotificationToken;
import com.ayn.states.realstate.entity.propertyFeature.Feature;
import com.ayn.states.realstate.entity.propertyFeature.PropertyFeatures;
import com.ayn.states.realstate.exception.UnauthorizedException;
import com.ayn.states.realstate.repository.compound.CompoundPostRepo;
import com.ayn.states.realstate.repository.compound.CompoundRepository;
import com.ayn.states.realstate.repository.compound.UnitMapRepository;
import com.ayn.states.realstate.repository.feature.FeatureRepository;
import com.ayn.states.realstate.repository.feature.StateFeatureRepo;
import com.ayn.states.realstate.repository.notification.NotificationTokenRepo;
import com.ayn.states.realstate.service.token.TokenService;
import com.google.firebase.messaging.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CompoundService implements CommandLineRunner {

    @Autowired
    private CompoundRepository compoundRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UnitMapRepository unitMapRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private CompoundPostRepo compoundPostRepo;

    @Value("${app.file-upload-dir}")
    private File basePath;

    @Value("${COMPOUND_BASE}")
    private String url;

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    @Autowired
    private NotificationTokenRepo notificationTokenRepo;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private StateFeatureRepo compoundFeatureRepo;



    @Transactional
    public Compound createCompound(
            CompoundDTO dto,
            MultipartFile coverImage,
            MultipartFile thumbnailImage,
            MultipartFile model3d,
            List<MultipartFile> unitMapsFiles,
            String token
    ) {
        // Extract user ID
        Integer userId = extractUserIdFromToken(token);

        // Create and populate compound entity
        Compound compound = createCompoundFromDto(dto, userId);

        // Upload files concurrently for better performance
        uploadCompoundFiles(compound, coverImage, thumbnailImage, model3d);

        // Save compound first to get the ID
        Compound savedCompound = compoundRepository.save(compound);

        // Batch save unit maps for better performance
        if (dto.unitMaps() != null && !dto.unitMaps().isEmpty()) {
            List<UnitMap> unitMapsToSave = createUnitMapsFromDto(
                    dto.unitMaps(),
                    unitMapsFiles,
                    savedCompound
            );

            // Batch save all unit maps at once
            unitMapRepository.saveAll(unitMapsToSave);
        }

        return savedCompound;
    }

    // Helper method to extract user ID from token
    private Integer extractUserIdFromToken(String token) {
        var decoded = tokenService.decodeToken(token.substring(7));
        if ("0".equals(decoded.getSubject())) {
            return decoded.getClaim("UnRegistered");
        }
        return Integer.parseInt(decoded.getSubject());
    }

    // Helper method to create compound entity from DTO
    private Compound createCompoundFromDto(CompoundDTO dto, Integer userId) {
        Compound compound = new Compound();
        applyDtoToEntity(dto, compound);
        compound.setCreateUser(userId);
        compound.setCreateAt(LocalDateTime.now());
        compound.setActive(dto.isActive());
        return compound;
    }

    // Helper method to upload files
    private void uploadCompoundFiles(
            Compound compound,
            MultipartFile coverImage,
            MultipartFile thumbnailImage,
            MultipartFile model3d
    ) {
        // Use parallel processing for file uploads if they're independent
        if (coverImage != null && !coverImage.isEmpty()) {
            compound.setCoverImageUrl(fileStorageService.storeFile(coverImage));
        }
        if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
            compound.setThumbnailUrl(fileStorageService.storeFile(thumbnailImage));
        }
        if (model3d != null && !model3d.isEmpty()) {
            compound.setModel3dUrl(fileStorageService.storeFile(model3d));
        }
    }

    // Helper method to create unit maps from DTO
    private List<UnitMap> createUnitMapsFromDto(
            List<UnitMapDTO> unitMapDtos,
            List<MultipartFile> unitMapsFiles,
            Compound savedCompound
    ) {
        List<UnitMap> unitMaps = new ArrayList<>();

        for (int i = 0; i < unitMapDtos.size(); i++) {
            UnitMapDTO mapDto = unitMapDtos.get(i);

            // Skip if marked for deletion
            if (mapDto.delete()) {
                continue;
            }

            UnitMap mapEntity = new UnitMap();
            mapEntity.setCompound(savedCompound);
            mapEntity.setDescription(mapDto.description()); // Fixed: use record accessor
            mapEntity.setSpecifications(mapDto.specifications()); // Fixed: use correct field

            // Upload unit map image if provided
            if (unitMapsFiles != null && i < unitMapsFiles.size() &&
                    !unitMapsFiles.get(i).isEmpty()) {
                mapEntity.setImageUrl(Collections.singletonList(fileStorageService.storeFile(unitMapsFiles.get(i))));
            }

            unitMaps.add(mapEntity);
        }

        return unitMaps;
    }

    // Optional: Async file upload for even better performance
    @Async
    public CompletableFuture<String> uploadFileAsync(MultipartFile file) {
        return CompletableFuture.completedFuture(fileStorageService.storeFile(file));
    }

    // Alternative implementation with async file uploads
    @Transactional
    public Compound createCompoundWithAsyncUploads(
            CompoundDTO dto,
            MultipartFile coverImage,
            MultipartFile thumbnailImage,
            MultipartFile model3d,
            List<MultipartFile> unitMapsFiles,
            String token, List<Long> features
    ) {
        Integer userId = extractUserIdFromToken(token);
        Compound compound = createCompoundFromDto(dto, userId);

        // Start file uploads asynchronously
        List<CompletableFuture<String>> uploadFutures = new ArrayList<>();

        if (coverImage != null && !coverImage.isEmpty()) {
            uploadFutures.add(uploadFileAsync(coverImage)
                    .thenApply(url -> { compound.setCoverImageUrl(url); return url; }));
        }

        if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
            uploadFutures.add(uploadFileAsync(thumbnailImage)
                    .thenApply(url -> { compound.setThumbnailUrl(url); return url; }));
        }

        if (model3d != null && !model3d.isEmpty()) {
            uploadFutures.add(uploadFileAsync(model3d)
                    .thenApply(url -> { compound.setModel3dUrl(url); return url; }));
        }

        // Wait for all uploads to complete
        CompletableFuture.allOf(uploadFutures.toArray(new CompletableFuture[0])).join();

        Set<Feature> featureSet = new HashSet<>(
                featureRepository.findAllById(features)
        );

        // Validate that all requested features exist
        if (featureSet.size() != features.size()) {
            throw new IllegalArgumentException("Some features not found");
        }
//        features.forEach(state::addFeature);

        Compound savedCompound = compoundRepository.save(compound);

        features.forEach(feature -> {
            compoundFeatureRepo.save(new PropertyFeatures(feature,savedCompound));
        });


        // Handle unit maps with async uploads
        if (dto.unitMaps() != null && !dto.unitMaps().isEmpty()) {
            List<CompletableFuture<UnitMap>> unitMapFutures = new ArrayList<>();

            for (int i = 0; i < dto.unitMaps().size(); i++) {
                final int index = i;
                UnitMapDTO mapDto = dto.unitMaps().get(i);

                if (mapDto.delete()) {
                    continue;
                }

                CompletableFuture<UnitMap> unitMapFuture = CompletableFuture.supplyAsync(() -> {
                    UnitMap mapEntity = new UnitMap();
                    mapEntity.setCompound(savedCompound);
                    mapEntity.setDescription(mapDto.description());
                    mapEntity.setSpecifications(mapDto.specifications());

                    if (unitMapsFiles != null && index < unitMapsFiles.size() &&
                            !unitMapsFiles.get(index).isEmpty()) {
                        String imageUrl = fileStorageService.storeFile(unitMapsFiles.get(index));
                        mapEntity.setImageUrl(Collections.singletonList(imageUrl));
                    }

                    return mapEntity;
                });

                unitMapFutures.add(unitMapFuture);
            }

            // Wait for all unit map processing and save in batch
            List<UnitMap> unitMaps = unitMapFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            unitMapRepository.saveAll(unitMaps);
        }

        return savedCompound;
    }

    @Transactional
    public Compound updateCompound(Long id, CompoundDTO dto, String token) {
        Compound compound = compoundRepository.findById(id)
                .orElseThrow(() -> new UnauthorizedException("not found"));

        Integer userId = null;
        if (tokenService.decodeToken(token.substring(7)).getSubject().equals("0")){
            userId=tokenService.decodeToken(token.substring(7)).getClaim("UnRegistered");
        }else
            userId=Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject());

        applyDtoToEntity(dto, compound);
        compound.setUpdateUser(userId);
        compound.setUpdateAt(LocalDateTime.now());
        return compoundRepository.save(compound);
    }


    @Transactional
    public Compound patchCompound(Long id, Map<String, Object> updates, String token) {
        Compound compound = compoundRepository.findById(id)
                .orElseThrow(() -> new UnauthorizedException("not found"));

        Integer userId = null;
        if (tokenService.decodeToken(token.substring(7)).getSubject().equals("0")){
            userId=tokenService.decodeToken(token.substring(7)).getClaim("UnRegistered");
        }else
            userId=Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject());


        updates.forEach((key, value) -> {
            switch (key) {
                case "coverImageUrl" -> compound.setCoverImageUrl((String) value);
                case "name" -> compound.setName((String) value);
                case "thumbnailUrl" -> compound.setThumbnailUrl((String) value);
                case "address" -> compound.setAddress((String) value);
                case "description" -> compound.setDescription((String) value);
                case "features" -> compound.setFeatures((List<String>) value);
                case "model3dUrl" -> compound.setModel3dUrl((String) value);
                case "latitude" -> compound.setLatitude((Double) value);
                case "longitude" -> compound.setLongitude((Double) value);
                case "contactNumber" -> compound.setContactNumber((String) value);
                case "active" -> compound.setActive((Boolean) value);
                case "socialLinks" -> {
                    Map<String, String> linksMap = (Map<String, String>) value;
                    SocialLinks links = compound.getSocialLinks() != null ? compound.getSocialLinks() : new SocialLinks();
                    if (linksMap.containsKey("facebook")) links.setFacebook(linksMap.get("facebook"));
                    if (linksMap.containsKey("instagram")) links.setInstagram(linksMap.get("instagram"));
                    if (linksMap.containsKey("twitter")) links.setTwitter(linksMap.get("twitter"));
                    if (linksMap.containsKey("website")) links.setWebsite(linksMap.get("website"));
                    compound.setSocialLinks(links);
                }
            }
        });

        compound.setUpdateUser(userId);
        compound.setUpdateAt(LocalDateTime.now());
        return compoundRepository.save(compound);
    }

    private void applyDtoToEntity(CompoundDTO dto, Compound compound) {
        compound.setCoverImageUrl(dto.coverImageUrl());
        compound.setName(dto.name());
        compound.setThumbnailUrl(dto.thumbnailUrl());
        compound.setAddress(dto.address());
        compound.setDescription(dto.description());
        compound.setFeatures(dto.features());
        compound.setModel3dUrl(dto.model3dUrl());
        compound.setLatitude(dto.latitude());
        compound.setLongitude(dto.longitude());
        compound.setContactNumber(dto.contactNumber());
        compound.setActive(dto.isActive());

        if (dto.socialLinks() != null) {
            SocialLinks links = new SocialLinks();
            links.setFacebook(dto.socialLinks().facebook());
            links.setInstagram(dto.socialLinks().instagram());
            links.setTwitter(dto.socialLinks().twitter());
            links.setWebsite(dto.socialLinks().website());
            compound.setSocialLinks(links);
        }
    }

    public ResponseEntity<?> getCompoundAttachment(String fileName) {
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
                    File file = new File(basePath, fileName); // fixed line
                    byte[] fileContent = Files.readAllBytes(file.toPath());

                    return ResponseEntity.ok()
                            .contentType(contentType)
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                            .body(fileContent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


    }



    public boolean publishCompound(Long Id, String token) {

        Compound compound = compoundRepository.findByActiveAndPublishedAtIsNull(Id).orElseThrow(() -> new UnauthorizedException("State not found"));

        compound.setApprovedUser(Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject()));
        compound.setApprovedAt(LocalDateTime.now());
        compoundRepository.save(compound);
        return true;
    }


    public List<Compound> unPublishedCompounds(int page) {
        List<Compound> compounds = compoundRepository.unPublishedCompounds(PageRequest.of((page > 0) ? page - 1 : 0, 10));
        return compounds;
    }

    public List<RealStatesController.LookUpData> getCompoundsFeature() {
        return jdbcClient.sql("""
                SELECT l.code, l.value  FROM lookup l where l.type_code=4""").query(RealStatesController.LookUpData.class).list();
    }

    public List<Compound> getCompoundsForUser(String token) {
        Jwt jwt = tokenService.decodeToken(token.substring(7));
        if (jwt.getClaim("scope").equals("DashboardUser")) {
            return compoundRepository.findByDashboardUserId(Long.parseLong(jwt.getSubject()));
        }
        else
            return null;

    }

    @Async
    public CompoundPost addPost(String title, String content, List<MultipartFile> images, long compoundId, String token) {

        Jwt jwt = tokenService.decodeToken(token.substring(7));
//        if (!jwt.getClaim("scope").equals("DashboardUser")) {
//            throw new UnauthorizedException("Not Authorized");
//        }
        compoundRepository.findById(compoundId)
                .orElseThrow(() -> new UnauthorizedException("Compound not found"));

        List<String> fileNames=new ArrayList<>();
        images.forEach(i->{
            fileNames.add(fileStorageService.storeFilePosts(i));
        });

        return compoundPostRepo.save(
                new CompoundPost(fileNames,title,content,compoundRepository.getReferenceById(compoundId),Integer.parseInt(jwt.getSubject()))
        );
    }

    @Async
    public CompoundPost updatePost(Long postId, String title, String content, List<MultipartFile> images, String token) {
        CompoundPost existingPost = compoundPostRepo.findById(postId)
                .orElseThrow(() -> new UnauthorizedException("Post not found"));

        Jwt jwt = tokenService.decodeToken(token.substring(7));
        if (!jwt.getClaim("scope").equals("DashboardUser")) {
            throw new UnauthorizedException("Not Authorized");
        }

        // Update only the fields that were provided
        if (title != null && !title.isBlank()) {
            existingPost.setTitle(title);
        }
        if (content != null && !content.isBlank()) {
            existingPost.setContent(content);
        }

        // If images are provided, replace them (or you could append)
        if (images != null && !images.isEmpty()) {
            List<String> fileNames = new ArrayList<>();
            for (MultipartFile img : images) {
                fileNames.add(fileStorageService.storeFilePosts(img));
            }
            existingPost.setImages(fileNames); // assuming you have `images` field as List<String>
        }
        existingPost.setUpdateAt(LocalDateTime.now());
        existingPost.setUpdateUser(Integer.parseInt(jwt.getSubject()));

        return compoundPostRepo.save(existingPost);
    }

    public boolean publishPost(Long postId, String token) {
        Jwt jwt = tokenService.decodeToken(token.substring(7));
        if (!jwt.getClaim("scope").equals("DashboardUser")) {
            throw new UnauthorizedException("Not Authorized");
        }
        CompoundPost referenceById = compoundPostRepo.getReferenceById(postId);
        referenceById.setApprovedAt(LocalDateTime.now());
        referenceById.setApprovedUser(Integer.parseInt(jwt.getSubject()));
        CompoundPost save = compoundPostRepo.save(referenceById);

        Notification notification = Notification
                .builder()
                .setTitle("منشور جديد")
                .setBody("منشور جديد من مجمع "+save.getCompound().getName())
                .build();

        Map<String, String> data = new HashMap<>();
        data.put("notification_typ", "3");
        data.put("content_available", "1");

        ApnsConfig apnsConfig = getApnsConfig();

        List<String> Fcmtoken = jdbcClient.sql("""
                select t.token from ST_APP_notification_token t
                join compound_followers f ON (f.user_id=t.user_id)
                where f.compound_id=:Id""").param("Id", save.getCompound().getId()).query(String.class).list();

        List<Message> messages = Fcmtoken.stream()
                .map(tokenStr -> Message.builder()
                        .setToken(tokenStr)
                        .setNotification(notification)
                        .setApnsConfig(apnsConfig)
                        .putAllData(data)
                        .build())
                .toList();

        if (!messages.isEmpty()) {
            firebaseMessaging.sendEachAsync(messages);
        }

//        save.getCompound().getFollowers().forEach(
//                e->{
//                    var Fcmtoken =notificationTokenRepo.findTokenByID(e.getUser().getUserId());
//                    if(Fcmtoken.isPresent()) {
//                        Message message = Message
//                                .builder()
//                                .setToken(Fcmtoken.get())
//                                .setNotification(notification)
//                                .setApnsConfig(apnsConfig)
//                                .putAllData(map)
//                                .build();
//
//                        firebaseMessaging.sendAsync(message);
//                    }
//                }
//        );

        return true;

    }

    @Override
    public void run(String... args) throws Exception {
        jdbcClient.sql("SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''))").update();
        compoundPostRepo.findById(1L);

    }

    public List<AllCompound> allCompounds(int page, Integer governate) {
        return jdbcClient.sql("""
          SELECT c.id AS compoundId, c.address, c.views_count AS viewsCount,CONCAT(:url,c.cover_image_url)  AS coverImage,CONCAT(:url,c.thumbnail_url) AS thumbnailUrl
          FROM compounds c WHERE c.is_active=1 AND c.approved_at IS NOT NULL
          AND (:governate IS NULL OR c.governorate = :governate)
           LIMIT :startFrom, 10
          """).param("url",url).param("governate",governate)
            .param("startFrom",((page - 1) * 10)).query(AllCompound.class).list();
    }

    public List<AllCompound> compoundsByGovernate(int governate) {
        return jdbcClient.sql("""
          SELECT c.id as compoundId, c.address, c.views_count AS viewsCount, CONCAT(:url,c.cover_image_url) AS coverImage, CONCAT(:url,c.thumbnail_url) AS thumbnailUrl
          FROM compounds c WHERE c.is_active=1 AND c.update_at IS NOT NULL AND c.governorate=:gov""")
                .param("url",url).param("gov",governate).query(AllCompound.class).list();
    }

    public CompoundDTO2 getCompound2(Long id, String token) {

        return compoundRepository.findCompoundWithUserFollow(id, Long.parseLong(tokenService.decodeToken(token.substring(7)).getSubject()));
    }

    private ApnsConfig getApnsConfig() {
        Map<String, Object> map2 = new HashMap<>();
        map2.put("content_available",1);
        ApsAlert apsAlert= ApsAlert.builder().setTitle("Zone").build();
        return ApnsConfig.builder()
                .setAps(Aps.builder().setSound("1").putAllCustomData(map2).setAlert(apsAlert).build()).build();
    }

    public List<Lookup> compoundNames() {
        return jdbcClient.sql("""
            SELECT c.id , c.name
          FROM compounds c""").query(Lookup.class).list();
    }
}

