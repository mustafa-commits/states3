package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.dto.states.StatesDTO;
import com.ayn.states.realstate.entity.states.States;
import com.ayn.states.realstate.service.sections.SectionService;
import com.ayn.states.realstate.service.states.StatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class RealStatesController implements SecuredRestController {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private StatesService service;

    @GetMapping("V1/api/appSections")
    public List<AppSections> appSections(){
        return sectionService.getAppSections();
    }


    @GetMapping("V1/api/StateForSale/{page}")
    public List<States> getStateForSale(@PathVariable int page){
        return service.getStateForSale(page);
    }

    @GetMapping("V1/api/StateForRent/{page}")
    public List<States> getStateForRent(@PathVariable int page){
        return service.getStateForRent(page);
    }

    @GetMapping("V1/api/StateForSale/{page}/{governate}")
    public List<States> getStateForSale(@PathVariable int page,@PathVariable int governate){
        return service.getStateForSale(page,governate);
    }

    @GetMapping("V1/api/StateForRent/{page}/{governate}")
    public List<States> getStateForRent(@PathVariable int page,@PathVariable int governate){
        return service.getStateForRent(page,governate);
    }




    public record AppSections(){}

}
