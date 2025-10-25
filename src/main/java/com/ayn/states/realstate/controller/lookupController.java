package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.entity.lookup.LookUp;
import com.ayn.states.realstate.entity.propertyFeature.FeatureType;
import com.ayn.states.realstate.repository.lookup.LookUpRepo;
import com.ayn.states.realstate.service.sections.SectionService;
import com.ayn.states.realstate.service.states.FeaturesService;
import io.swagger.v3.oas.annotations.headers.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
public class lookupController implements SecuredRestController {

    @Autowired
    private FeaturesService stateFeaturesService;

    @Autowired
    private SectionService sectionService;

    @GetMapping("V1/api/appSections")
    public List<RealStatesController.AppSections> appSections() {
        return sectionService.getAppSections();
    }


    @GetMapping("/V1/api/AllGovernate")
//    @CacheEvict(value = {"SaleStates","RentalStates"})
    public List<RealStatesController.LookUpData> getAllGovernate() {
        return stateFeaturesService.getAllGovernate();
    }

    @GetMapping("/V1/api/ListFeatures")
    public List<FeaturesService.FeatureDTO> getStateFeatures() {
        return stateFeaturesService.getStateFeatures();
    }
    @GetMapping("/V1/api/FeatureImage/{fileName}")
    public ResponseEntity<?> getFeatureImage(@PathVariable String fileName) {
        return stateFeaturesService.getFeatureImage(fileName);
    }

    @PostMapping("/V1/api/createFeature")
    public FeaturesService.FeatureDTO addFeature(@RequestHeader(name = "Authorization") String token, @RequestParam String featureName,
                                                 @RequestParam FeatureType featureType, @RequestParam MultipartFile image) throws IOException {
            return stateFeaturesService.createFeature(featureName,featureType,image,token);
    }


    @GetMapping("/V1/api/propertyType")
    public List<LookUp> getPropertyType() {
        return stateFeaturesService.getPropertyType();
    }

    @GetMapping("/V1/api/ownershipType")
    public List<RealStatesController.LookUpData> getownershipType() {
        return stateFeaturesService.getOwnershipType();
    }


}
