package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.dto.compound.*;
import com.ayn.states.realstate.entity.compound.Compound;
import com.ayn.states.realstate.entity.compound.CompoundPost;
import com.ayn.states.realstate.entity.lookup.LookUp;
import com.ayn.states.realstate.repository.compound.CompoundRepository;
import com.ayn.states.realstate.repository.compound.CompoundRepositoryJdbc;
import com.ayn.states.realstate.service.compound.CompoundService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class CompoundController implements SecuredRestController  {

    @Autowired
    private CompoundService compoundService;

    @Autowired
    private CompoundRepositoryJdbc compoundRepositoryJdbc;

    @Autowired
    private CompoundRepository compoundRepository;


    @GetMapping("/V1/api/Compounds/features")
    public List<RealStatesController.LookUpData> getCompoundsFeature() {
        return compoundService.getCompoundsFeature();
    }


    @PostMapping(value = "/V1/api/AddCompound", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Compound createCompound(
            @RequestPart("compound") String compoundJson,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage,
            @RequestPart(value = "model3d", required = false) MultipartFile model3d,
            @RequestPart(value = "unitMaps", required = false) List<MultipartFile> unitMapsFiles,
            @RequestHeader(name = "Authorization") String token
    ) throws JsonProcessingException {

        return compoundService.createCompoundWithAsyncUploads(
                new ObjectMapper().readValue(compoundJson, CompoundDTO.class)
                , coverImage, thumbnailImage, model3d, unitMapsFiles, token);
    }


//    @PutMapping("/V1/api/EditCompound/{id}")
    public Compound updateCompound(
            @PathVariable Long id,
            @RequestBody CompoundDTO dto,
            @RequestHeader(name = "Authorization") String token
    ) {
        return compoundService.updateCompound(id, dto, token);
    }

    @PatchMapping("/V2/api/EditCompound/{id}")
    public Compound patchCompound(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates,
            @RequestHeader(name = "Authorization") String token
    ) {
        return compoundService.patchCompound(id, updates, token);
    }


    @GetMapping("/V1/api/AllCompounds/{page}")
    public List<AllCompound> allCompounds(@PathVariable int page, @RequestParam Integer governate) {
        return compoundService.allCompounds(page,governate);
//        return compoundRepositoryJdbc.findActiveCompoundImages();
    }

    @GetMapping("/V1/api/compoundNames")
    public List<Lookup> compoundNames(){
        return compoundService.compoundNames();
    }

    @GetMapping("/V1/api/CompoundsByGovernate/{governate}")
    public List<AllCompound> compoundsByGovernate(@PathVariable int governate) {
        return compoundService.compoundsByGovernate(governate);
//        return compoundRepositoryJdbc.findActiveCompoundImages();
    }




//    @GetMapping("/V1/api/CompoundById/{id}")
//    public ResponseEntity<CompoundResponseDTO> getCompound(@PathVariable Long id) throws JsonProcessingException {
//        CompoundResponseDTO dto = compoundRepositoryJdbc.findCompoundById(id);
//        return (dto != null) ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
//    }

    @GetMapping("/V1/api/CompoundById/{id}")
    public CompoundDTO2 getCompound2(@RequestHeader(name = "Authorization") String token,@PathVariable Long id) {
        return compoundService.getCompound2(id,token);
    }

    @GetMapping("/V1/api/unPublishedCompounds/{page}")
    public List<Compound> unPublishedCompounds(@PathVariable int page) {
        return compoundService.unPublishedCompounds(page+1);
    }

    @GetMapping("V1/api/PublishCompound/{Id}")
    public boolean publishCompound(@RequestHeader(name = "Authorization") String token, @PathVariable Long Id) {
        return compoundService.publishCompound(Id, token);
    }


    @GetMapping("/V1/api/CompoundAttachment/{fileName}")
//    @Cacheable(cacheNames = "CompoundAttachment", key = "'file_' + #fileName")
    public ResponseEntity<?> getCompoundAttachment(@PathVariable String fileName) {
        return compoundService.getCompoundAttachment(fileName);
    }

    @PostMapping("/V1/api/myCompound")
    public List<Compound> myCompound(@RequestHeader(name = "Authorization") String token) {
        return compoundService.getCompoundsForUser(token);
    }

    @PostMapping("/V1/api/compound/AddPost")
    public CompoundPost addPost(@RequestHeader(name = "Authorization") String token,
                                @RequestParam List<MultipartFile> images,
                                @RequestParam String title,
                                @RequestParam String content,
                                @RequestParam(name = "compound_id") long compoundId){
        return compoundService.addPost(title,content,images,compoundId,token);
    }

    @GetMapping("V1/api/compound/PublishPost/{postId}")
    public boolean publishPost(@RequestHeader(name = "Authorization") String token, @PathVariable Long postId) {
        return compoundService.publishPost(postId, token);
    }

    @PatchMapping("/V1/api/compound/UpdatePost")
    public CompoundPost updatePost(@RequestHeader(name = "Authorization") String token,
                                   @RequestParam Long postId,
                                   @RequestParam(required = false) String title,
                                   @RequestParam(required = false) String content,
                                   @RequestParam(required = false) List<MultipartFile> images) {
        return compoundService.updatePost(postId, title, content, images, token);
    }

}
