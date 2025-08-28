package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.dto.location.Area;
import com.ayn.states.realstate.dto.location.Province;
import com.ayn.states.realstate.service.location.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LocationController implements SecuredRestController {

    private final LocationService locationService;

    @GetMapping("/V1/api/provinces")
    public List<Province> getProvinces() {
        return locationService.getAllProvinces();
    }

    @GetMapping("/V1/api/areas")
    public List<Area> getAreas(@RequestParam int provinceId) {
        return locationService.getAreasByProvinceId(provinceId);
    }

    @GetMapping("/V1/api/searchProvinces")
    public List<Province> searchProvinces(@RequestParam String name) {
        return locationService.searchProvincesByName(name);
    }

    @GetMapping("/V1/api/searchAreas")
    public List<Area> searchAreas(@RequestParam int provinceId,@RequestParam String name) {
        return locationService.searchAreasByName(provinceId,name);
    }

}

