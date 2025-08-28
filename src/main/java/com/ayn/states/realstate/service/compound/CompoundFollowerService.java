package com.ayn.states.realstate.service.compound;

import com.ayn.states.realstate.entity.compound.Compound;
import com.ayn.states.realstate.entity.compound.CompoundFollower;
import com.ayn.states.realstate.entity.user.Users;
import com.ayn.states.realstate.exception.UnauthorizedException;
import com.ayn.states.realstate.repository.compound.CompoundFollowerRepository;
import com.ayn.states.realstate.repository.compound.CompoundRepository;
import com.ayn.states.realstate.repository.user.UsersRepo;
import com.ayn.states.realstate.service.token.TokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CompoundFollowerService {

    @Autowired
    private CompoundFollowerRepository followerRepository;

    @Autowired
    private CompoundRepository compoundRepository;

    @Autowired
    private UsersRepo userRepository;

    @Autowired
    private TokenService tokenService;


    @Transactional
    public boolean followCompound(Long compoundId, String token) {
        long userId=Long.parseLong(tokenService.decodeToken(token.substring(7)).getSubject());
        if (followerRepository.existsByCompoundAndUser(compoundId, userId)) {
            throw new UnauthorizedException("User already follows this compound");
        }
        followerRepository.save(new CompoundFollower(
                userRepository.getReferenceById(userId),compoundRepository.getReferenceById(compoundId),
                true,LocalDateTime.now()
        ));
        return true;
    }

    @Transactional
    public int unfollowCompound(Long compoundId, String token) {
        if (followerRepository.deleteByCompoundAndUser(compoundId, Long.parseLong(tokenService.decodeToken(token.substring(7)).getSubject())) == 1) {
            return 1;
        }
        throw new UnauthorizedException("User is not following this compound");
    }
}