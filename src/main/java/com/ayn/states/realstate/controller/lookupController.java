package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.service.sections.SectionService;
import com.ayn.states.realstate.service.states.StateFeaturesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class lookupController implements SecuredRestController {

    @Autowired
    private StateFeaturesService stateFeaturesService;

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

    @GetMapping("/V1/api/StateFeatures")
    public List<RealStatesController.LookUpData> getStateFeatures() {
        return stateFeaturesService.getStateFeatures();
    }


}
