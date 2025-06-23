package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.service.sections.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class RealStatesController implements SecuredRestController {

    @Autowired
    private SectionService sectionService;

    @GetMapping("V1/api/appSections")
    public List<AppSections> appSections(){
        return sectionService.getAppSections();
    }




    public record AppSections(){}

}
