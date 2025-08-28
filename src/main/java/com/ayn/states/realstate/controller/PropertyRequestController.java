package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.entity.propertyRequest.PropertyRequest;
import com.ayn.states.realstate.repository.propertyRequest.PropertyRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PropertyRequestController implements SecuredRestController {


    @Autowired
    private PropertyRequestRepository repo;

    // إنشاء طلب جديد
    @PostMapping("/V1/api/makePropertyRequest")
    public PropertyRequest createRequest(@RequestHeader(name = "Authorization") String token,
                                         @RequestBody PropertyRequest request) {
        if(request.getExpiryDate() == null) {
            request.setExpiryDate(request.getCreatedAt().plusDays(30));
        }
        return repo.save(request);
    }

    @GetMapping("/V1/api/GetActiveRequests")
    public List<PropertyRequest> getActiveRequests() {
        return repo.findActiveRequests();
    }

}
