package com.ayn.states.realstate.service.states;


import com.ayn.states.realstate.controller.RealStatesController;
import com.ayn.states.realstate.dto.states.StatesDTO;
import com.ayn.states.realstate.entity.att.Attachments;
import com.ayn.states.realstate.entity.lookup.UrlImageType;
import com.ayn.states.realstate.entity.states.States;
import com.ayn.states.realstate.enums.StateType;
import com.ayn.states.realstate.exception.UnauthorizedException;
import com.ayn.states.realstate.repository.attachment.AttachmentsRepo;
import com.ayn.states.realstate.repository.state.StatesRepo;
import com.ayn.states.realstate.service.token.TokenService;
import com.tinify.Source;
import com.tinify.Tinify;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class StatesService {


    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private StatesRepo statesRepository;

    @Autowired
    private AttachmentsRepo attachmentsRepo;

//    @Autowired
//    private StatesMapper statesMapper;

    @Autowired
    private TokenService tokenService;

    @Value("${FOLDER_PATH}")
    private String basePath;

    @Value("${StateAttLink}")
    private String stateLink;


    @Cacheable(value = "SaleStates", key = "#page")
    public List<StatesDTO> getStateForSale(int page) {
//        return statesRepository.findByStateTypeAndIsActiveTrue(StateType.FOR_SALE, PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "publishedAt")));

        return jdbcClient.sql("""
                    
                        SELECT s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                               s.num_of_bath_rooms, s.num_of_storey, s.price, s.longitude, s.latitude, s.is_active
                                        ,s.created_user, s.published_at, s.country, s.governorate, s.state_type,GROUP_CONCAT(CONCAT(:link, a.url_image) SEPARATOR  ',') AS attachments
                    FROM states s
                    left JOIN attachment a ON s.state_id = a.state_id
                    WHERE s.is_active = 1  AND s.state_type = 'FOR_SALE' AND s.published_at IS NOT NULL
                      group by s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                               s.num_of_bath_rooms, s.num_of_storey, s.price, s.longitude, s.latitude, s.is_active
                                        ,s.created_user, s.published_at, s.country, s.governorate, s.state_type
                    ORDER BY s.published_at DESC
                    LIMIT :offset, 10""")
                .param("offset",(page - 1) * 10).param("link",stateLink).query(StatesDTO.class).list();


//                .map(statesMapper::toDto);

//        List<StatesDTO> content = jdbcClient.sql("""
//            SELECT s.state_id, s.description, s.area, s.num_of_rooms,
//                   s.garage_size, s.num_of_bath_rooms, s.num_of_storey,
//                   s.price, s.longitude, s.latitude, s.is_active,
//                   s.created_at, s.published_at, s.country, s.governorate,
//                   s.state_type,
//                   CONCAT(:link, a.url_image) as full_url_image,
//                   a.url_image_type
//            FROM states s
//            LEFT JOIN attachment a ON s.state_id = a.state_id
//            WHERE s.state_type = :type
//            AND s.is_active = true
//            ORDER BY s.published_at DESC
//            LIMIT :page*10 OFFSET 0
//            """)
//                .param("link",stateLink) // Base URL for attachments
//                .param(StateType.FOR_SALE.name())
//                .param("page",page)
//                .param("type",StateType.FOR_SALE.ordinal())
//                .query((rs, rowNum) -> StatesDTO.builder()
//                        .stateId(rs.getLong("state_id"))
//                        .description(rs.getString("description"))
//                        .area(rs.getInt("area"))
//                        .numOfRooms(rs.getInt("num_of_rooms"))
//                        .price(rs.getLong("price"))
//                        .longitude(rs.getInt("longitude"))
//                        .latitude(rs.getInt("latitude"))
//                        .stateType(StateType.valueOf(rs.getString("state_type")))
//                        .country(rs.getInt("country"))
//                        .governorate(rs.getInt("governorate"))
//                        .attachments(rs.getString("thumbnail_url"))
//                        .build())
//
//                .list();



    }

    /**
     * Overloaded method with default sorting
     */
//    public Page<StatesDTO> getStateForSale(int page, int size) {
//        return getStateForSale(page, size, "createdAt", "desc");
//    }




    @Cacheable(value = "RentalStates", key = "#page")
    public List<StatesDTO> getStateForRent(int page) {
        List<StatesDTO> list = jdbcClient.sql("""
                        
                            SELECT s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                                   s.num_of_bath_rooms, s.num_of_storey, s.price, s.longitude, s.latitude, s.is_active
                                            ,s.created_user, s.published_at, s.country, s.governorate, s.state_type,GROUP_CONCAT(CONCAT(:link, a.url_image)) AS attachments
                        FROM states s
                        left JOIN attachment a ON s.state_id = a.state_id
                        WHERE s.is_active = 1  AND s.state_type = 'FOR_RENT' AND s.published_at IS NOT NULL
                          group by s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                                   s.num_of_bath_rooms, s.num_of_storey, s.price, s.longitude, s.latitude, s.is_active
                                            ,s.created_user, s.published_at, s.country, s.governorate, s.state_type
                        ORDER BY s.published_at DESC
                        LIMIT :offset, 10""")
                .param("offset", (page - 1) * 10).param("link", stateLink).query(StatesDTO.class).list();

        System.out.print(list.get(0).getAttachments());
        return list;

//        return statesRepository.findByStateTypeAndIsActiveTrue(StateType.FOR_RENT, PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "publishedAt")));
                //.map(statesMapper::toDto);
    }

    public List<States> getStateForRent(int page, int governate) {
        return statesRepository.findByStateTypeAndIsActiveTrueWithGovernate(StateType.FOR_RENT,governate , PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "publishedAt")));
    }

    public List<States> getStateForSale(int page, int governate) {
        return statesRepository.findByStateTypeAndIsActiveTrueWithGovernate(StateType.FOR_SALE,governate , PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "publishedAt")));
    }

    public List<RealStatesController.LookUpData> getAllGovernate() {
        return jdbcClient.sql("""
                        SELECT code, value from lookup""").query(RealStatesController.LookUpData.class).list();
    }



    @CacheEvict(value = {"SaleStates","RentalStates"})
    public boolean addNewState(@NotBlank(message = "Description is required") String description, @Min(value = 1, message = "Area must be greater than 0") int area, @Min(value = 0, message = "Number of rooms cannot be negative") int numOfRooms, @Min(value = 0, message = "Garage size cannot be negative") int garageSize, @Min(value = 0, message = "Number of bathrooms cannot be negative") int numOfBathRooms, @Min(value = 0, message = "Number of storeys cannot be negative") int numOfStorey, @Min(value = 1, message = "Price must be greater than 0") long price, double longitude, double latitude, @NotNull(message = "Country is required") int country, @NotNull(message = "Governorate is required") int governorate, @NotNull(message = "State type is required") StateType stateType, List<MultipartFile> attachments, String token) throws IOException {



        List<String> newfileNames = new ArrayList<>();
        List<String> filespath = new ArrayList<>();
        List<Attachments> urlImagesList = new ArrayList<>();

        var state = statesRepository.save(
                new States(description, area, numOfRooms, garageSize, numOfBathRooms, numOfStorey, price, longitude, latitude,
                        Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject()), null, null, null, country, governorate, stateType)
        );

        if (attachments != null) {

            for (int i = 0; i < attachments.size(); i++) {
                MultipartFile file = attachments.get(i);
                if (Objects.equals(file.getContentType(), "video/mp4")) {
                    if (file.getSize() > 13288000)
                        throw new UnauthorizedException("حجم الملف غير مسموح به اكبر من 12MB");
                } else {
                    if (file.getSize() > 4998000)
                        throw new UnauthorizedException("حجم الملف غير مسموح به اكبر من 5MB");
                }
                if (file != null) {
                    String originalFilename = file.getOriginalFilename();
                    newfileNames.add(System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf(".")));
                    filespath.add(basePath + newfileNames.get(i));
                    urlImagesList.add(new Attachments(newfileNames.get(i),null, UrlImageType.MAIN_PIC,state));

                    file.transferTo(new File(filespath.get(i)));
                    Tinify.setKey("28MYcgnnzHSNYkgQLt17tNMn80RnHk2c");
                    Source source = Tinify.fromFile(new File(filespath.get(i)).getPath());
                    source.toFile(filespath.get(i));
                }
            }
            attachmentsRepo.saveAll(urlImagesList);



            return true;
        }


    return false;
    }

    public List<StatesDTO> unPublishedStates(int page) {
        return jdbcClient.sql("""
                    
                        SELECT s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                               s.num_of_bath_rooms, s.num_of_storey, s.price, s.longitude, s.latitude, s.is_active
                                        ,s.created_user, s.published_at, s.country, s.governorate, s.state_type,GROUP_CONCAT(CONCAT(:link, a.url_image) SEPARATOR  ',') AS attachments
                    FROM states s
                    left JOIN attachment a ON s.state_id = a.state_id
                    WHERE s.is_active = 1  AND s.published_at IS NULL
                      group by s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                               s.num_of_bath_rooms, s.num_of_storey, s.price, s.longitude, s.latitude, s.is_active
                                        ,s.created_user, s.published_at, s.country, s.governorate, s.state_type
                    ORDER BY s.published_at DESC
                    LIMIT :offset, 10""")
                .param("offset",(page - 1) * 10).param("link",stateLink).query(StatesDTO.class).list();
    }

    public boolean PublishedStates(Long stateId, String token) {

        States state = statesRepository.findByActiveAndPublishedAtIsNull(stateId).orElseThrow(() -> new UnauthorizedException("State not found"));

        state.setPublishedBy(Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject()));
        state.setPublishedAt(LocalDateTime.now());
        statesRepository.save(state);
        return true;
    }
}
