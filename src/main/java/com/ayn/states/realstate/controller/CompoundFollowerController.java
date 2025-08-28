package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.service.compound.CompoundFollowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class CompoundFollowerController implements SecuredRestController {


    @Autowired
    private CompoundFollowerService followerService;


    @PostMapping("/V1/api/compounds/{compoundId}/follow")
    public boolean followCompound(
            @PathVariable Long compoundId,
            @RequestHeader(name = "Authorization") String token
    ) {
        return followerService.followCompound(compoundId, token);
    }

    @DeleteMapping("/V1/api/compounds/{compoundId}/unfollow")
    public int unfollowCompound(
            @PathVariable Long compoundId,
            @RequestHeader(name = "Authorization") String token
    ) {
        return followerService.unfollowCompound(compoundId, token);
    }


}
