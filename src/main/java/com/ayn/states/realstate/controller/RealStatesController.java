package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.dto.states.StatesDTO;
import com.ayn.states.realstate.enums.Category;
import com.ayn.states.realstate.enums.PaymentMethod;
import com.ayn.states.realstate.service.attachment.AttachmentService;
import com.ayn.states.realstate.service.states.StatesService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
public class RealStatesController implements SecuredRestController {


    @Autowired
    private StatesService service;

    @Autowired
    private AttachmentService attachmentService;

    @GetMapping("V1/api/StateForSale/{page}")
    public List<StatesDTO> getStateForSale(@RequestHeader(name = "Authorization") String token,@PathVariable int page) {
        return service.getStateForSale(page,token);
    }

    @GetMapping("V1/api/StateForRent/{page}")
    public List<StatesDTO> getStateForRent(@RequestHeader(name = "Authorization") String token,@PathVariable int page) {
        return service.getStateForRent(page,token);
    }

    @GetMapping("V1/api/unPublishedStates/{page}")
    public List<StatesDTO> unPublishedStates(@PathVariable int page) {
        return service.unPublishedStates(page);
    }

    @GetMapping("V1/api/PublishState/{stateId}")
    public boolean publishedStates(@RequestHeader(name = "Authorization") String token, @PathVariable Long stateId) {
        return service.PublishedStates(stateId, token);
    }


    @GetMapping("V1/api/StateForSale/{page}/{governate}")
    public List<StatesDTO> getStateForSale(@RequestHeader(name = "Authorization") String token,@PathVariable int page, @PathVariable int governate) {
        return service.getStateForSale(page, governate,token);
    }

    @GetMapping("V1/api/StateForRent/{page}/{governate}")
    public List<StatesDTO> getStateForRent(@RequestHeader(name = "Authorization") String token,@PathVariable int page, @PathVariable int governate) {
        return service.getStateForRent(page, governate,token);
    }


    @PostMapping("/V1/api/addNewState")
    public boolean addNewState(@RequestHeader(name = "Authorization") String token,
                               @RequestParam @NotBlank(message = "Description is required") String description,
                               @RequestParam @Min(value = 1, message = "Area must be greater than 0") int area,
                               @RequestParam(name = "num_of_bed_rooms") @Min(value = 0, message = "Number of rooms cannot be negative") int numOfBedRooms,
                               @RequestParam(name = "garage_size") @Min(value = 0, message = "Garage size cannot be negative") int garageSize,
                               @RequestParam(name = "num_of_bath_rooms") @Min(value = 0, message = "Number of bathrooms cannot be negative") int numOfBathRooms,
                               @RequestParam(name = "num_of_storey") @Min(value = 0, message = "Number of storeys cannot be negative") int numOfStorey,
                               @RequestParam @Min(value = 1, message = "Price must be greater than 0") long price,
                               @RequestParam double longitude,
                               @RequestParam double latitude,
//                               @RequestParam @NotNull(message = "Country is required") int country,
                               @RequestParam @NotNull(message = "Governorate is required") int governorate,
                               @RequestParam(name = "category") @NotNull(message = "State type is required") Category category,
                               @RequestParam(name = "property_type") int propertyType,
                               @RequestParam(name = "property_sub_type") int propertySubType,
                               @RequestParam(name = "ownership_type") int ownershipType,
                               @RequestParam(name = "building_age") int buildingAge,
                               @RequestParam(name = "address") String address,
                               @RequestParam(name = "payment_method") PaymentMethod paymentMethod,
                               @RequestParam(name = "features", required = false) List<Integer> features,


                               @RequestParam List<MultipartFile> attachments) throws IOException {
        return service.addNewState(description,
                area, numOfBedRooms, garageSize, numOfBathRooms, numOfStorey, price, longitude, latitude,  governorate, category, attachments, token,
                propertyType, ownershipType, buildingAge, address, paymentMethod, features,propertySubType);
    }


    @GetMapping("/V1/api/stateAttachment/{fileName}")
//    @Cacheable(cacheNames = "StateAttachment", key = "'file_' + #fileName")
    public ResponseEntity<?> getStateAttachment(@PathVariable String fileName) {
        return attachmentService.getStateAttachment(fileName);
    }

    @GetMapping("/V1/api/resetCache")
    public void resetCache() {

    }


    public record AppSections() {
    }

    public record LookUpData(
            int code,
            String value
    ) {
    }

}
