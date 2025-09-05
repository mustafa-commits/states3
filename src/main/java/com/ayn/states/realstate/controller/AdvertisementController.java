package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.entity.advertisement.Advertisement;
import com.ayn.states.realstate.entity.advertisement.AdvertisementType;
import com.ayn.states.realstate.service.advertisement.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class AdvertisementController implements SecuredRestController {


    @Autowired
    private AdvertisementService service;

    // Get all active ads
    @GetMapping("/V1/api/getAds")
    public List<Advertisement> getAds() {
        return service.getActiveAds();
    }

    // Create new ad (admin only ideally)
    @PostMapping("/V1/api/createAd")
    public Advertisement createAd(
            @RequestParam String title,
            @RequestParam(required = false) String body,
            @RequestParam(required = false) Integer targetId,
            @RequestParam AdvertisementType type,
            @RequestParam boolean isActive,
            @RequestParam int period,
            @RequestParam Long advertiserPhone,
            @RequestParam MultipartFile image,
            @RequestHeader(name = "Authorization") String token
            ) throws IOException {
        return service.saveAd(title,targetId,type,image,isActive,token,period,advertiserPhone,body);
    }

    @GetMapping("/V1/api/AdvertisementAttachment/{fileName}")
    public ResponseEntity<?> getAdvAttachment(@PathVariable String fileName) {
        return service.getAttachment(fileName);
    }


}
