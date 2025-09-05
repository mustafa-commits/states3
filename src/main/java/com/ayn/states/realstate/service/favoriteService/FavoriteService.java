package com.ayn.states.realstate.service.favoriteService;


import com.ayn.states.realstate.dto.states.StatesDTO;
import com.ayn.states.realstate.entity.fav.UserActions;
import com.ayn.states.realstate.entity.unregisterUsers.UnregisteredUser;
import com.ayn.states.realstate.enums.ActionType;
import com.ayn.states.realstate.repository.state.StatesRepo;
import com.ayn.states.realstate.repository.unRegisteredUsers.UnregisteredUserRepo;
import com.ayn.states.realstate.repository.user.UsersRepo;
import com.ayn.states.realstate.repository.userAction.UserActionRepo;
import com.ayn.states.realstate.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteService {


    @Autowired
    private UnregisteredUserRepo unregisteredUserRepo;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserActionRepo userActionRepo;

    @Autowired
    private StatesRepo statesRepo;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private JdbcClient jdbcClient;

    @Value("${StateAttLink}")
    private String stateLink;


    public long registerUser() {

         return unregisteredUserRepo.save(new UnregisteredUser(
                Instant.now().getEpochSecond()
        )).getTempIdentifier();

    }


    public boolean addViewState(String token, long l, ActionType actionType) {
        if (tokenService.decodeToken(token.substring(7)).getSubject().equals("0")) {
            userActionRepo.save(new UserActions(
                statesRepo.getReferenceById(l),unregisteredUserRepo.getReferenceById(
                    tokenService.decodeToken(token.substring(7)).getClaim("UnRegistered")
            ),actionType, LocalDateTime.now()
            ));
        }else
        userActionRepo.save(new UserActions(
                usersRepo.getReferenceById(Long.valueOf(tokenService.decodeToken(token.substring(7)).getSubject())),statesRepo.getReferenceById(l),actionType, LocalDateTime.now()
        ));


        return true;
    }

    public List<StatesDTO> getMyFav(String token, int page) {
            Long userId = null;
            if (tokenService.decodeToken(token.substring(7)).getSubject().equals("0")){
                userId=tokenService.decodeToken(token.substring(7)).getClaim("UnRegistered");
            }else
                userId=Long.parseLong(tokenService.decodeToken(token.substring(7)).getSubject());

            return jdbcClient.sql("""
                        SELECT s.state_id AS stateId, s.description, s.area, s.num_of_rooms AS numOfRooms ,s.garage_size AS garageSize,
                                               s.num_of_bath_rooms AS numOfBathRooms, s.num_of_storey AS numOfStorey,s.num_of_bed_rooms AS numOfBedrooms, l.value AS propertyType
                                               , l3.value AS propertySubType, l2.value AS ownershipType
                               ,s.building_age AS buildingAge , s.price, s.longitude, s.latitude
                                        , s.published_at AS publishedAt, c.name AS country, g.name_ar AS governorate , s.state_type AS category ,
                                          s.address , s.payment_method AS paymentMethod ,GROUP_CONCAT(DISTINCT CONCAT(:link, a.url_image) SEPARATOR  ',') AS attachments,
                                         GROUP_CONCAT(DISTINCT p.feature_code SEPARATOR  ',') AS features,
                                         CONCAT(z.first_name, ' ',z.last_name) AS publisherName,
                                         z.phone AS publisherPhone,
                                         count(distinct ua.id)  as viewCount,
                                        case when MAX(ua2.id) is not null then true else false end as isFavorite
                    FROM states s
                    left JOIN attachment a ON s.state_id = a.state_id
                    JOIN fnd_governorates g ON s.governorate = g.code
                    JOIN fnd_countries c ON s.country = c.code
                    JOIN lookup l ON s.property_type = l.code AND l.type_code=1 AND l.parent_id IS NULL
                    JOIN lookup l2 ON s.ownership_type = l2.code AND l2.type_code=2
                    JOIN lookup l3 ON s.property_sub_type = l3.code AND l3.type_code=1
                    LEFT JOIN property_features p ON s.state_id = p.state_id
                    JOIN zone_users z ON s.created_user = z.user_id
                    LEFT JOIN user_actions ua ON ua.state_id = s.state_id AND ua.action_type = 'VIEW'
                    JOIN user_actions ua2 ON ua2.state_id = s.state_id AND ua2.action_type = 'FAVORITE' and ( ua2.app_user_id =:userId or ua2.unregistered_id =:userId)
                    WHERE s.is_active = 1  AND s.published_at IS NOT NULL
                      group by s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                               s.num_of_bath_rooms, s.num_of_storey,s.property_type,s.ownership_type, s.price, s.longitude, s.latitude, s.is_active,s.building_age
                                        ,s.created_user, s.published_at, c.name, g.name_ar , s.state_type,s.address,s.payment_method
                    ORDER BY s.published_at DESC
                    LIMIT :offset, 10""")
                    .param("offset",(page - 1) * 10).param("link",stateLink)
                    .param("userId",userId).query(StatesDTO.class).list();
    }


//    public boolean addFavState(String token, long l) {
//    }
}



