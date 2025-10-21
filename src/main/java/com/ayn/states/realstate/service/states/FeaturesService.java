package com.ayn.states.realstate.service.states;


import com.ayn.states.realstate.controller.RealStatesController;
import com.ayn.states.realstate.entity.lookup.LookUp;
import com.ayn.states.realstate.entity.propertyFeature.Feature;
import com.ayn.states.realstate.entity.propertyFeature.FeatureType;
import com.ayn.states.realstate.repository.feature.FeatureRepository;
import com.ayn.states.realstate.repository.lookup.LookUpRepo;
import com.ayn.states.realstate.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeaturesService {


    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private LookUpRepo lookUpRepo;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private TokenService tokenService;


    private final String UPLOAD_DIR = "uploads/";


    public List<FeatureDTO> getStateFeatures() {

        return featureRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

//        return jdbcClient.sql("""
//                SELECT l.code, l.value  FROM lookup l where l.type_code=3""").query(RealStatesController.LookUpData.class).list();
    }

    public FeatureDTO createFeature(String featureName, FeatureType featureType, MultipartFile image, String token) throws IOException {
        if (featureRepository.existsByName(featureName)) {
            throw new IllegalArgumentException("Feature already exists");
        }


        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file
        String filename = System.currentTimeMillis() + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
        Path filePath = uploadPath.resolve(filename);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Feature feature = new Feature(
                featureName,
                featureType,
                Long.parseLong(tokenService.decodeToken(token.substring(7)).getSubject()),
                filename
        );

        Feature saved = featureRepository.save(feature);
        return convertToDTO(saved);
    }

    public List<RealStatesController.LookUpData> getAllGovernate() {
        return jdbcClient.sql("""
                SELECT f.code, f.name_ar value from fnd_governorates f""").query(RealStatesController.LookUpData.class).list();
    }


    public List<LookUp> getPropertyType() {
        return lookUpRepo.findByTypeCodeCustom();
    }

    public List<RealStatesController.LookUpData> getOwnershipType() {
        return jdbcClient.sql("""
                SELECT l.code, l.value  FROM lookup l where l.type_code=2""").query(RealStatesController.LookUpData.class).list();
    }


    private FeatureDTO convertToDTO(Feature feature) {
        return new FeatureDTO (
                feature.getFeatureId(),
                feature.getName()
        );
    }



    public record FeatureDTO (
            long code,
            String value
    ){}

    public record CreateFeatureDTO (
         String featureName,
         String description

    ){}
}
