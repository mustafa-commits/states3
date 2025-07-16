package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.dto.states.StatesDTO;
import com.ayn.states.realstate.enums.ActionType;
import com.ayn.states.realstate.service.favoriteService.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class FavoriteController implements SecuredRestController {


    @Autowired
    private FavoriteService favoriteService;



//    @PostMapping("/V1/api/addFavState")
//    public boolean addFavState(@RequestHeader(name = "Authorization") String token,@RequestBody AddViewFav addViewFav){
//        return favoriteService.addFavState(token,addViewFav.stateId());
//    }


    @PostMapping("/V1/api/addViewFavState")
    public boolean addViewState(@RequestHeader(name = "Authorization") String token,@RequestBody AddViewFav addViewFav){
        return favoriteService.addViewState(token,addViewFav.stateId(),addViewFav.userAction());
    }

    @GetMapping("/V1/api/getMyFav/{page}")
    public List<StatesDTO> getMyFav(@RequestHeader(name = "Authorization") String token,@PathVariable int page){
        return favoriteService.getMyFav(token,page);
    }


    public record AddViewFav(
            long stateId,
            ActionType userAction
    ){}


}
