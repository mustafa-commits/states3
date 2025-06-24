package com.ayn.states.realstate.service.states;


import com.ayn.states.realstate.dto.states.StatesDTO;
import com.ayn.states.realstate.dto.states.StatesListingDTO;
import com.ayn.states.realstate.entity.states.States;
import com.ayn.states.realstate.enums.StateType;
import com.ayn.states.realstate.mapper.StatesMapper;
import com.ayn.states.realstate.repository.state.StatesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatesService {


    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private StatesRepo statesRepository;

    @Autowired
    private StatesMapper statesMapper;

    @Value("${StateAttLink}")
    private String stateLink;


    @Cacheable(value = "SaleStates", key = "#page")
    public Page<StatesDTO> getStateForSale(int page) {
//        return statesRepository.findByStateTypeAndIsActiveTrue(StateType.FOR_SALE, PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "publishedAt")))
//                .map(statesMapper::toDto);

        List<StatesDTO> content = jdbcClient.sql("""
            SELECT s.state_id, s.description, s.area, s.num_of_rooms,
                   s.garage_size, s.num_of_bath_rooms, s.num_of_storey,
                   s.price, s.longitude, s.latitude, s.is_active,
                   s.created_at, s.published_at, s.country, s.governorate,
                   s.state_type,
                   CONCAT(:link, a.url_image) as full_url_image,
                   a.url_image_type
            FROM states s
            LEFT JOIN attachment a ON s.state_id = a.state_id
            WHERE s.state_type = :type
            AND s.is_active = true
            ORDER BY s.published_at DESC
            LIMIT :page*10 OFFSET 0
            """)
                .param("link",stateLink) // Base URL for attachments
                .param(StateType.FOR_SALE.name())
                .param("page",page)
                .param("type",StateType.FOR_SALE.ordinal())
                .query((rs, rowNum) -> StatesDTO.builder()
                        .stateId(rs.getLong("state_id"))
                        .description(rs.getString("description"))
                        .area(rs.getInt("area"))
                        .numOfRooms(rs.getInt("num_of_rooms"))
                        .price(rs.getLong("price"))
                        .longitude(rs.getInt("longitude"))
                        .latitude(rs.getInt("latitude"))
                        .stateType(StateType.valueOf(rs.getString("state_type")))
                        .country(rs.getInt("country"))
                        .governorate(rs.getInt("governorate"))
                        .attachments(rs.getString("thumbnail_url"))
                        .build())

                .list();



    }

    /**
     * Overloaded method with default sorting
     */
//    public Page<StatesDTO> getStateForSale(int page, int size) {
//        return getStateForSale(page, size, "createdAt", "desc");
//    }




    @Cacheable(value = "RentalStates", key = "#page + '_' + #size + '_' + #sortBy + '_' + #sortDirection")
    public Page<StatesDTO> getStateForRent(int page) {

        return statesRepository.findByStateTypeAndIsActiveTrue(StateType.FOR_RENT, PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "publishedAt")))
                .map(statesMapper::toDto);
    }
}
