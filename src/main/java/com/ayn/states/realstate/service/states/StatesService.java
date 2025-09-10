package com.ayn.states.realstate.service.states;


import com.ayn.states.realstate.dto.NearestStateRequest;
import com.ayn.states.realstate.dto.states.StatesDTO;
import com.ayn.states.realstate.dto.states.StatesDTO2;
import com.ayn.states.realstate.entity.att.Attachments;
import com.ayn.states.realstate.entity.lookup.UrlImageType;
import com.ayn.states.realstate.entity.propertyFeature.PropertyFeatures;
import com.ayn.states.realstate.entity.states.States;
import com.ayn.states.realstate.enums.PaymentMethod;
import com.ayn.states.realstate.enums.Category;
import com.ayn.states.realstate.exception.UnauthorizedException;
import com.ayn.states.realstate.repository.attachment.AttachmentsRepo;
import com.ayn.states.realstate.repository.feature.StateFeatureRepo;
import com.ayn.states.realstate.repository.state.StatesRepo;
import com.ayn.states.realstate.service.token.TokenService;
import com.tinify.Source;
import com.tinify.Tinify;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private StateFeatureRepo stateFeatureRepo;

    @Value("${FOLDER_PATH}")
    private String basePath;

    @Value("${StateAttLink}")
    private String stateLink;


//    @Cacheable(value = "SaleStates", key = "#page")
    public List<StatesDTO> getStateForSale(int page, String token) {
//        return statesRepository.findByStateTypeAndIsActiveTrue(Category.FOR_SALE, PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "publishedAt")));

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
                                        , s.published_at AS publishedAt,  g.name_ar AS governorate , s.state_type AS category ,
                                          s.address , s.payment_method AS paymentMethod ,GROUP_CONCAT(DISTINCT CONCAT(:link, a.url_image) SEPARATOR  ',') AS attachments,
                                         GROUP_CONCAT(DISTINCT p.feature_code SEPARATOR  ',') AS features,
                                         CONCAT(z.first_name, ' ',z.last_name) AS publisherName,
                                         z.phone AS publisherPhone,
                                         count(distinct ua.id)  as viewCount,
                                        case when MAX(ua2.id) is not null then true else false end as isFavorite
                    FROM states s
                    left JOIN attachment a ON s.state_id = a.state_id
                    JOIN fnd_governorates g ON s.governorate = g.code
                    JOIN lookup l ON s.property_type = l.code AND l.type_code=1 AND l.parent_id IS NULL
                    JOIN lookup l2 ON s.ownership_type = l2.code AND l2.type_code=2
                    JOIN lookup l3 ON s.property_sub_type = l3.code AND l3.type_code=1
                    LEFT JOIN property_features p ON s.state_id = p.state_id
                    JOIN zone_users z ON s.created_user = z.user_id
                    LEFT JOIN user_actions ua ON ua.state_id = s.state_id AND ua.action_type = 'VIEW'
                    LEFT JOIN user_actions ua2 ON ua2.state_id = s.state_id AND ua2.action_type = 'FAVORITE' and ( ua2.app_user_id =:userId or ua2.unregistered_id =:userId)
                    WHERE s.is_active = 1  AND s.state_type = 'FOR_SALE' AND s.published_at IS NOT NULL
                      group by s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                               s.num_of_bath_rooms, s.num_of_storey,s.property_type,s.ownership_type, s.price, s.longitude, s.latitude, s.is_active,s.building_age
                                        ,s.created_user, s.published_at,  g.name_ar , s.state_type,s.address,s.payment_method
                    ORDER BY s.published_at DESC
                    LIMIT :offset, 10""")
                .param("offset",(page - 1) * 10).param("link",stateLink)
                .param("userId",userId).query(StatesDTO.class).list();


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
//                .param(Category.FOR_SALE.name())
//                .param("page",page)
//                .param("type",Category.FOR_SALE.ordinal())
//                .query((rs, rowNum) -> StatesDTO.builder()
//                        .stateId(rs.getLong("state_id"))
//                        .description(rs.getString("description"))
//                        .area(rs.getInt("area"))
//                        .numOfRooms(rs.getInt("num_of_rooms"))
//                        .price(rs.getLong("price"))
//                        .longitude(rs.getInt("longitude"))
//                        .latitude(rs.getInt("latitude"))
//                        .category(Category.valueOf(rs.getString("state_type")))
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




//    @Cacheable(value = "RentalStates", key = "#page")
    public List<StatesDTO> getStateForRent(int page, String token) {

        Long userId = null;
        if (tokenService.decodeToken(token.substring(7)).getSubject().equals("0")){
            userId=tokenService.decodeToken(token.substring(7)).getClaim("UnRegistered");
        }else
            userId=Long.parseLong(tokenService.decodeToken(token.substring(7)).getSubject());
        List<StatesDTO> list = jdbcClient.sql("""
                            SELECT s.state_id AS stateId, s.description, s.area, s.num_of_rooms AS numOfRooms ,s.garage_size AS garageSize,
                                               s.num_of_bath_rooms AS numOfBathRooms, s.num_of_storey AS numOfStorey,s.num_of_bed_rooms AS numOfBedrooms, l.value AS propertyType 
                                               , l3.value AS propertySubType ,l2.value AS ownershipType
                               ,s.building_age AS buildingAge , s.price, s.longitude, s.latitude
                                        , s.published_at AS publishedAt,  g.name_ar AS governorate , s.state_type AS category ,
                                          s.address , s.payment_method AS paymentMethod ,GROUP_CONCAT(DISTINCT CONCAT(:link, a.url_image) SEPARATOR  ',') AS attachments,
                                         GROUP_CONCAT(DISTINCT p.feature_code SEPARATOR  ',') AS features,
                                         CONCAT(z.first_name, ' ',z.last_name) AS publisherName,
                                         z.phone AS publisherPhone,
                                         count(distinct ua.id)  as viewCount,
                                        case when MAX(ua2.id) is not null then true else false end as isFavorite
                        FROM states s
                        left JOIN attachment a ON s.state_id = a.state_id
                        JOIN fnd_governorates g ON s.governorate = g.code
                        JOIN lookup l ON s.property_type = l.code AND l.type_code=1
                        JOIN lookup l2 ON s.ownership_type = l2.code AND l2.type_code=2
                        JOIN lookup l3 ON s.property_sub_type = l3.code AND l3.type_code=1
                        LEFT JOIN property_features p ON s.state_id = p.state_id
                        JOIN zone_users z ON s.created_user = z.user_id
                        LEFT JOIN user_actions ua ON ua.state_id = s.state_id AND ua.action_type = 'VIEW'
                        LEFT JOIN user_actions ua2 ON ua2.state_id = s.state_id AND ua2.action_type = 'FAVORITE' and ( ua2.app_user_id =:userId or ua2.unregistered_id =:userId)
                        WHERE s.is_active = 1  AND s.state_type = 'FOR_RENT' AND s.published_at IS NOT NULL
                          group by s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                                   s.num_of_bath_rooms, s.num_of_storey,s.property_type,s.ownership_type, s.price, s.longitude, s.latitude, s.is_active
                                            ,s.created_user, s.published_at, g.name_ar , s.state_type, s.state_type ,s.address,s.payment_method,s.building_age ,l3.value
                        ORDER BY s.published_at DESC
                        LIMIT :startFrom, 10""")
                .param("startFrom", (page - 1) * 10).param("link", stateLink)
                .param("userId",userId).query(StatesDTO.class).list();

        System.out.print(list.get(0).getAttachments());
        return list;

//        return statesRepository.findByStateTypeAndIsActiveTrue(Category.FOR_RENT, PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "publishedAt")));
                //.map(statesMapper::toDto);
    }

    public List<StatesDTO> getStateForRent(int page, int governate,String token) {

        Long userId = null;
        if (tokenService.decodeToken(token.substring(7)).getSubject().equals("0")){
            userId=tokenService.decodeToken(token.substring(7)).getClaim("UnRegistered");
        }else
            userId=Long.parseLong(tokenService.decodeToken(token.substring(7)).getSubject());
        return jdbcClient.sql("""
                            SELECT s.state_id AS stateId, s.description, s.area, s.num_of_rooms AS numOfRooms ,s.garage_size AS garageSize,
                                               s.num_of_bath_rooms AS numOfBathRooms, s.num_of_storey AS numOfStorey,s.num_of_bed_rooms AS numOfBedrooms, l.value AS propertyType
                                               , l3.value AS propertySubType ,l2.value AS ownershipType
                               ,s.building_age AS buildingAge , s.price, s.longitude, s.latitude
                                        , s.published_at AS publishedAt,  g.name_ar AS governorate , s.state_type AS category ,
                                          s.address , s.payment_method AS paymentMethod ,GROUP_CONCAT(DISTINCT CONCAT(:link, a.url_image) SEPARATOR  ',') AS attachments,
                                         GROUP_CONCAT(DISTINCT p.feature_code SEPARATOR  ',') AS features,
                                         CONCAT(z.first_name, ' ',z.last_name) AS publisherName,
                                         z.phone AS publisherPhone,
                                         count(distinct ua.id)  as viewCount,
                                        case when MAX(ua2.id) is not null then true else false end as isFavorite
                        FROM states s
                        left JOIN attachment a ON s.state_id = a.state_id
                        JOIN fnd_governorates g ON s.governorate = g.code
                        JOIN lookup l ON s.property_type = l.code AND l.type_code=1
                        JOIN lookup l2 ON s.ownership_type = l2.code AND l2.type_code=2
                        JOIN lookup l3 ON s.property_sub_type = l3.code AND l3.type_code=1
                        LEFT JOIN property_features p ON s.state_id = p.state_id
                        JOIN zone_users z ON s.created_user = z.user_id
                        LEFT JOIN user_actions ua ON ua.state_id = s.state_id AND ua.action_type = 'VIEW'
                        LEFT JOIN user_actions ua2 ON ua2.state_id = s.state_id AND ua2.action_type = 'FAVORITE' and ( ua2.app_user_id =:userId or ua2.unregistered_id =:userId)
                        WHERE s.is_active = 1  AND s.state_type = 'FOR_RENT' AND s.published_at IS NOT NULL AND s.governorate = :governate
                          group by s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                                   s.num_of_bath_rooms, s.num_of_storey,s.property_type,s.ownership_type, s.price, s.longitude, s.latitude, s.is_active
                                            ,s.created_user, s.published_at, g.name_ar , s.state_type, s.state_type ,s.address,s.payment_method,s.building_age ,l3.value
                        ORDER BY s.published_at DESC
                        LIMIT :startFrom, 10""")
                .param("startFrom", (page - 1) * 10).param("link", stateLink).param("governate",governate)
                .param("userId",userId).query(StatesDTO.class).list();
//        return statesRepository.findByStateTypeAndIsActiveTrueWithGovernate(Category.FOR_RENT,governate , PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "publishedAt")));
    }

    public List<StatesDTO> getStateForSale(int page, int governate, String token) {

        Long userId = null;
        if (tokenService.decodeToken(token.substring(7)).getSubject().equals("0")){
            userId=tokenService.decodeToken(token.substring(7)).getClaim("UnRegistered");
        }else
            userId=Long.parseLong(tokenService.decodeToken(token.substring(7)).getSubject());

        return jdbcClient.sql("""
                            SELECT s.state_id AS stateId, s.description, s.area, s.num_of_rooms AS numOfRooms ,s.garage_size AS garageSize,
                                               s.num_of_bath_rooms AS numOfBathRooms, s.num_of_storey AS numOfStorey,s.num_of_bed_rooms AS numOfBedrooms, l.value AS propertyType
                                               , l3.value AS propertySubType ,l2.value AS ownershipType
                               ,s.building_age AS buildingAge , s.price, s.longitude, s.latitude
                                        , s.published_at AS publishedAt, g.name_ar AS governorate , s.state_type AS category ,
                                          s.address , s.payment_method AS paymentMethod ,GROUP_CONCAT(DISTINCT CONCAT(:link, a.url_image) SEPARATOR  ',') AS attachments,
                                         GROUP_CONCAT(DISTINCT p.feature_code SEPARATOR  ',') AS features,
                                         CONCAT(z.first_name, ' ',z.last_name) AS publisherName,
                                         z.phone AS publisherPhone,
                                         count(distinct ua.id)  as viewCount,
                                        case when MAX(ua2.id) is not null then true else false end as isFavorite
                        FROM states s
                        left JOIN attachment a ON s.state_id = a.state_id
                        JOIN fnd_governorates g ON s.governorate = g.code
                        JOIN lookup l ON s.property_type = l.code AND l.type_code=1
                        JOIN lookup l2 ON s.ownership_type = l2.code AND l2.type_code=2
                        JOIN lookup l3 ON s.property_sub_type = l3.code AND l3.type_code=1
                        LEFT JOIN property_features p ON s.state_id = p.state_id
                        JOIN zone_users z ON s.created_user = z.user_id
                        LEFT JOIN user_actions ua ON ua.state_id = s.state_id AND ua.action_type = 'VIEW'
                        LEFT JOIN user_actions ua2 ON ua2.state_id = s.state_id AND ua2.action_type = 'FAVORITE' and ( ua2.app_user_id =:userId or ua2.unregistered_id =:userId)
                        WHERE s.is_active = 1  AND s.state_type = 'FOR_SALE' AND s.published_at IS NOT NULL AND s.governorate = :governate
                          group by s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                                   s.num_of_bath_rooms, s.num_of_storey,s.property_type,s.ownership_type, s.price, s.longitude, s.latitude, s.is_active
                                            ,s.created_user, s.published_at, g.name_ar , s.state_type, s.state_type ,s.address,s.payment_method,s.building_age ,l3.value
                        ORDER BY s.published_at DESC
                        LIMIT :startFrom, 10""")
                .param("startFrom", (page - 1) * 10).param("link", stateLink).param("governate",governate)
                .param("userId",userId).query(StatesDTO.class).list();
//        return statesRepository.findByStateTypeAndIsActiveTrueWithGovernate(Category.FOR_SALE,governate , PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "publishedAt")));
    }

    public List<StatesDTO> getALLState(int page, Integer governate, String token,Integer propertySubType, Integer propertyType,String sortBy, String order) {

        Long userId = null;
        if (tokenService.decodeToken(token.substring(7)).getSubject().equals("0")){
            userId=tokenService.decodeToken(token.substring(7)).getClaim("UnRegistered");
        }else
            userId=Long.parseLong(tokenService.decodeToken(token.substring(7)).getSubject());

        return jdbcClient.sql("""
                            SELECT s.state_id AS stateId, s.description, s.area, s.num_of_rooms AS numOfRooms ,s.garage_size AS garageSize,
                                               s.num_of_bath_rooms AS numOfBathRooms, s.num_of_storey AS numOfStorey,s.num_of_bed_rooms AS numOfBedrooms, l.value AS propertyType
                                               , l3.value AS propertySubType ,l2.value AS ownershipType
                               ,s.building_age AS buildingAge , s.price, s.longitude, s.latitude
                                        , s.published_at AS publishedAt,  g.name_ar AS governorate , s.state_type AS category ,
                                          s.address , s.payment_method AS paymentMethod ,GROUP_CONCAT(DISTINCT CONCAT(:link, a.url_image) SEPARATOR  ',') AS attachments,
                                         GROUP_CONCAT(DISTINCT p.feature_code SEPARATOR  ',') AS features,
                                         CONCAT(z.first_name, ' ',z.last_name) AS publisherName,
                                         z.phone AS publisherPhone,
                                         count(distinct ua.id)  as viewCount,
                                        case when MAX(ua2.id) is not null then true else false end as isFavorite
                        FROM states s
                        left JOIN attachment a ON s.state_id = a.state_id
                        JOIN fnd_governorates g ON s.governorate = g.code
                        JOIN lookup l ON s.property_type = l.code AND l.type_code=1
                        JOIN lookup l2 ON s.ownership_type = l2.code AND l2.type_code=2
                        JOIN lookup l3 ON s.property_sub_type = l3.code AND l3.type_code=1
                        LEFT JOIN property_features p ON s.state_id = p.state_id
                        JOIN zone_users z ON s.created_user = z.user_id
                        LEFT JOIN user_actions ua ON ua.state_id = s.state_id AND ua.action_type = 'VIEW'
                        LEFT JOIN user_actions ua2 ON ua2.state_id = s.state_id AND ua2.action_type = 'FAVORITE' and ( ua2.app_user_id =:userId or ua2.unregistered_id =:userId)
                        WHERE s.is_active = 1 AND s.published_at IS NOT NULL
                        AND (:governate IS NULL OR s.governorate = :governate)
                        AND (:propertyType IS NULL OR s.property_type = :propertyType)
                        AND (:propertySubType IS NULL OR s.property_sub_type = :propertySubType)
                        group by s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                                   s.num_of_bath_rooms, s.num_of_storey,s.property_type,s.ownership_type, s.price, s.longitude, s.latitude, s.is_active
                                            ,s.created_user, s.published_at, g.name_ar , s.state_type, s.state_type ,s.address,s.payment_method,s.building_age ,l3.value
                        ORDER BY CASE WHEN :sortBy = 'date' AND :order = 'asc' THEN s.published_at END ASC,
                                                        CASE WHEN :sortBy = 'date' AND :order = 'desc' THEN s.published_at END DESC,
                                                        CASE WHEN :sortBy = 'price' AND :order = 'asc' THEN s.price END ASC,
                                                        CASE WHEN :sortBy = 'price' AND :order = 'desc' THEN s.price END DESC
                        LIMIT :startFrom, 10""")
                .param("startFrom", (page - 1) * 10).param("link", stateLink).param("governate",governate)
                .param("propertyType",propertyType).param("propertySubType",propertySubType)
                .param("sortBy",sortBy).param("order",order)
                .param("userId",userId).query(StatesDTO.class).list();
    }




//    @CacheEvict(value = {"SaleStates","RentalStates"})
    public boolean addNewState(@NotBlank(message = "Description is required") String description, @Min(value = 1, message = "Area must be greater than 0") int area, @Min(value = 0, message = "Number of rooms cannot be negative") int numOfRooms, @Min(value = 0, message = "Garage size cannot be negative") int garageSize, @Min(value = 0, message = "Number of bathrooms cannot be negative") int numOfBathRooms, @Min(value = 0, message = "Number of storeys cannot be negative") int numOfStorey, @Min(value = 1, message = "Price must be greater than 0") long price, double longitude, double latitude, @NotNull(message = "Governorate is required") int governorate, @NotNull(message = "State type is required") Category category, List<MultipartFile> attachments, String token,
                               int propertyType, int ownershipType, int buildingAge, String address, PaymentMethod paymentMethod, List<Integer> features, int propertySubType) throws IOException {



        List<String> newfileNames = new ArrayList<>();
        List<String> filespath = new ArrayList<>();
        List<Attachments> urlImagesList = new ArrayList<>();

        var state = statesRepository.save(
                new States(description, area, numOfRooms, garageSize, numOfBathRooms, numOfStorey, price, longitude, latitude,
                        Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject()), null, null, null,  governorate, category,
                        propertyType, ownershipType, buildingAge,address,paymentMethod,propertySubType)
        );


            features.forEach(feature -> {
                stateFeatureRepo.save(new PropertyFeatures(feature,state));
            });

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
                if (!file.isEmpty()) {
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
                    
                        SELECT s.state_id AS stateId, s.description, s.area, s.num_of_rooms AS numOfRooms ,s.garage_size AS garageSize,
                                               s.num_of_bath_rooms AS numOfBathRooms, s.num_of_storey AS numOfStorey,s.num_of_bed_rooms AS numOfBedrooms, l.value AS propertyType
                                               , l3.value AS propertySubType ,l2.value AS ownershipType
                               ,s.building_age AS buildingAge , s.price, s.longitude, s.latitude
                                        , s.published_at AS publishedAt, g.name_ar AS governorate , s.state_type AS category ,
                                          s.address , s.payment_method AS paymentMethod ,GROUP_CONCAT(DISTINCT CONCAT(:link, a.url_image) SEPARATOR  ',') AS attachments,
                                         GROUP_CONCAT(DISTINCT p.feature_code SEPARATOR  ',') AS features,
                                         CONCAT(z.first_name, ' ',z.last_name) AS publisherName,
                                         z.phone AS publisherPhone,
                                         0  as viewCount,
                                        false isFavorite
                    FROM states s
                    left JOIN attachment a ON s.state_id = a.state_id
                    JOIN fnd_governorates g ON s.governorate = g.code
                    JOIN lookup l ON s.property_type = l.code AND l.type_code=1
                    JOIN lookup l2 ON s.ownership_type = l2.code AND l2.type_code=2
                    JOIN lookup l3 ON s.property_sub_type = l3.code AND l3.type_code=1
                    LEFT JOIN property_features p ON s.state_id = p.state_id
                    JOIN zone_users z ON s.created_user = z.user_id
                    WHERE s.is_active = 1  -- AND s.published_at IS NULL
                      group by s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                               s.num_of_bath_rooms, s.num_of_storey, s.price,s.property_type,s.ownership_type , s.longitude, s.latitude, s.is_active
                                        ,s.created_user, s.published_at, g.name_ar , s.state_type, s.state_type ,s.address,s.payment_method,s.building_age,l3.value
                    ORDER BY s.published_at DESC
                    LIMIT :startFrom, 10""")
                .param("startFrom",(page - 1) * 10).param("link",stateLink).query(StatesDTO.class).list();
    }

    public boolean PublishedStates(Long stateId, String token) {

        States state = statesRepository.findByActiveAndPublishedAtIsNull(stateId).orElseThrow(() -> new UnauthorizedException("State not found"));

        state.setPublishedBy(Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject()));
        state.setPublishedAt(LocalDateTime.now());
        statesRepository.save(state);
        return true;
    }

    public List<StatesDTO2> getNearestStatesForRent(int page, @Valid NearestStateRequest nearestStateRequest, String token) {
        Long userId = null;
        if (tokenService.decodeToken(token.substring(7)).getSubject().equals("0")){
            userId=tokenService.decodeToken(token.substring(7)).getClaim("UnRegistered");
        }else
            userId=Long.parseLong(tokenService.decodeToken(token.substring(7)).getSubject());

        return jdbcClient.sql("""
                            SELECT s.state_id AS stateId, s.description, s.area, s.num_of_rooms AS numOfRooms ,s.garage_size AS garageSize,
                                               s.num_of_bath_rooms AS numOfBathRooms, s.num_of_storey AS numOfStorey,s.num_of_bed_rooms AS numOfBedrooms, l.value AS propertyType
                                               , l3.value AS propertySubType ,l2.value AS ownershipType
                               ,s.building_age AS buildingAge , s.price, s.longitude, s.latitude
                                        , s.published_at AS publishedAt,  g.name_ar AS governorate , s.state_type AS category ,
                                          s.address , s.payment_method AS paymentMethod ,GROUP_CONCAT(DISTINCT CONCAT(:link, a.url_image) SEPARATOR  ',') AS attachments,
                                         GROUP_CONCAT(DISTINCT p.feature_code SEPARATOR  ',') AS features,
                                         CONCAT(z.first_name, ' ',z.last_name) AS publisherName,
                                         z.phone AS publisherPhone,
                                         count(distinct ua.id)  as viewCount,
                                        case when MAX(ua2.id) is not null then true else false end as isFavorite,
                                        count(distinct ua3.id)  as favCount,
                                        ST_Distance_Sphere(s.location, ST_SRID(POINT(:lng, :lat),4326))/ 1000 AS distance
                        FROM states s
                        left JOIN attachment a ON s.state_id = a.state_id
                        JOIN fnd_governorates g ON s.governorate = g.code
                        JOIN lookup l ON s.property_type = l.code AND l.type_code=1
                        JOIN lookup l2 ON s.ownership_type = l2.code AND l2.type_code=2
                        JOIN lookup l3 ON s.property_sub_type = l3.code AND l3.type_code=1
                        LEFT JOIN property_features p ON s.state_id = p.state_id
                        JOIN zone_users z ON s.created_user = z.user_id
                        LEFT JOIN user_actions ua ON ua.state_id = s.state_id AND ua.action_type = 'VIEW'
                        LEFT JOIN user_actions ua2 ON ua2.state_id = s.state_id AND ua2.action_type = 'FAVORITE' and ( ua2.app_user_id =:userId or ua2.unregistered_id =:userId)
                        LEFT JOIN user_actions ua3 ON ua3.state_id = s.state_id AND ua3.action_type = 'FAVORITE'
                        WHERE s.is_active = 1 AND s.published_at IS NOT NULL AND s.state_type = 'FOR_RENT'
                          group by s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                                   s.num_of_bath_rooms, s.num_of_storey,s.property_type,s.ownership_type, s.price, s.longitude, s.latitude, s.is_active
                                            ,s.created_user, s.published_at, g.name_ar , s.state_type, s.state_type ,s.address,s.payment_method,s.building_age ,l3.value
                        ORDER BY distance
                        LIMIT :startFrom, 10""")
                .param("startFrom", (page - 1) * 10).param("link", stateLink).param("lng",nearestStateRequest.longitude()).param("lat",nearestStateRequest.latitude())
                .param("userId",userId).query(StatesDTO2.class).list();


    }

    public List<StatesDTO2> getNearestStatesForSale(int page, @Valid NearestStateRequest nearestStateRequest, String token) {
        Long userId = null;
        if (tokenService.decodeToken(token.substring(7)).getSubject().equals("0")){
            userId=tokenService.decodeToken(token.substring(7)).getClaim("UnRegistered");
        }else
            userId=Long.parseLong(tokenService.decodeToken(token.substring(7)).getSubject());

        return jdbcClient.sql("""
                            SELECT s.state_id AS stateId, s.description, s.area, s.num_of_rooms AS numOfRooms ,s.garage_size AS garageSize,
                                               s.num_of_bath_rooms AS numOfBathRooms, s.num_of_storey AS numOfStorey,s.num_of_bed_rooms AS numOfBedrooms, l.value AS propertyType
                                               , l3.value AS propertySubType ,l2.value AS ownershipType
                               ,s.building_age AS buildingAge , s.price, s.longitude, s.latitude
                                        , s.published_at AS publishedAt,  g.name_ar AS governorate , s.state_type AS category ,
                                          s.address , s.payment_method AS paymentMethod ,GROUP_CONCAT(DISTINCT CONCAT(:link, a.url_image) SEPARATOR  ',') AS attachments,
                                         GROUP_CONCAT(DISTINCT p.feature_code SEPARATOR  ',') AS features,
                                         CONCAT(z.first_name, ' ',z.last_name) AS publisherName,
                                         z.phone AS publisherPhone,
                                         count(distinct ua.id)  as viewCount,
                                        case when MAX(ua2.id) is not null then true else false end as isFavorite,
                                        count(distinct ua3.id)  as favCount,
                                        ST_Distance_Sphere(s.location, ST_SRID(POINT(:lng, :lat),4326))/ 1000 AS distance
                        FROM states s
                        left JOIN attachment a ON s.state_id = a.state_id
                        JOIN fnd_governorates g ON s.governorate = g.code
                        JOIN lookup l ON s.property_type = l.code AND l.type_code=1
                        JOIN lookup l2 ON s.ownership_type = l2.code AND l2.type_code=2
                        JOIN lookup l3 ON s.property_sub_type = l3.code AND l3.type_code=1
                        LEFT JOIN property_features p ON s.state_id = p.state_id
                        JOIN zone_users z ON s.created_user = z.user_id
                        LEFT JOIN user_actions ua ON ua.state_id = s.state_id AND ua.action_type = 'VIEW'
                        LEFT JOIN user_actions ua2 ON ua2.state_id = s.state_id AND ua2.action_type = 'FAVORITE' and ( ua2.app_user_id =:userId or ua2.unregistered_id =:userId)
                        LEFT JOIN user_actions ua3 ON ua3.state_id = s.state_id AND ua3.action_type = 'FAVORITE'
                        WHERE s.is_active = 1 AND s.published_at IS NOT NULL AND s.state_type = 'FOR_SALE'
                          group by s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                                   s.num_of_bath_rooms, s.num_of_storey,s.property_type,s.ownership_type, s.price, s.longitude, s.latitude, s.is_active
                                            ,s.created_user, s.published_at, g.name_ar , s.state_type, s.state_type ,s.address,s.payment_method,s.building_age ,l3.value
                        ORDER BY distance
                        LIMIT :startFrom, 10""")
                .param("startFrom", (page - 1) * 10).param("link", stateLink).param("lng",nearestStateRequest.longitude()).param("lat",nearestStateRequest.latitude())
                .param("userId",userId).query(StatesDTO2.class).list();


    }

    public List<StatesDTO> getmyState(String token) {


        return jdbcClient.sql("""
                            SELECT s.state_id AS stateId, s.description, s.area, s.num_of_rooms AS numOfRooms ,s.garage_size AS garageSize,
                                               s.num_of_bath_rooms AS numOfBathRooms, s.num_of_storey AS numOfStorey,s.num_of_bed_rooms AS numOfBedrooms, l.value AS propertyType
                                               , l3.value AS propertySubType ,l2.value AS ownershipType
                               ,s.building_age AS buildingAge , s.price, s.longitude, s.latitude
                                        , s.published_at AS publishedAt, g.name_ar AS governorate , s.state_type AS category ,
                                          s.address , s.payment_method AS paymentMethod ,GROUP_CONCAT(DISTINCT CONCAT(:link, a.url_image) SEPARATOR  ',') AS attachments,
                                         GROUP_CONCAT(DISTINCT p.feature_code SEPARATOR  ',') AS features,
                                         CONCAT(z.first_name, ' ',z.last_name) AS publisherName,
                                         z.phone AS publisherPhone,
                                         count(distinct ua.id)  as viewCount,
                                        case when MAX(ua2.id) is not null then true else false end as isFavorite
                        FROM states s
                        left JOIN attachment a ON s.state_id = a.state_id
                        JOIN fnd_governorates g ON s.governorate = g.code
                        JOIN lookup l ON s.property_type = l.code AND l.type_code=1
                        JOIN lookup l2 ON s.ownership_type = l2.code AND l2.type_code=2
                        JOIN lookup l3 ON s.property_sub_type = l3.code AND l3.type_code=1
                        LEFT JOIN property_features p ON s.state_id = p.state_id
                        JOIN zone_users z ON s.created_user = z.user_id
                        LEFT JOIN user_actions ua ON ua.state_id = s.state_id AND ua.action_type = 'VIEW'
                        LEFT JOIN user_actions ua2 ON ua2.state_id = s.state_id AND ua2.action_type = 'FAVORITE' and ( ua2.app_user_id =:userId or ua2.unregistered_id =:userId)
                        WHERE s.is_active = 1  AND s.published_at IS NOT NULL AND s.createdUser=:userId
                          group by s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                                   s.num_of_bath_rooms, s.num_of_storey,s.property_type,s.ownership_type, s.price, s.longitude, s.latitude, s.is_active
                                            ,s.created_user, s.published_at, g.name_ar , s.state_type, s.state_type ,s.address,s.payment_method,s.building_age ,l3.value
                        ORDER BY s.published_at DESC
                     """)
                .param("link", stateLink)
                .param("userId",Long.parseLong(tokenService.decodeToken(token.substring(7)).getSubject())).query(StatesDTO.class).list();
    }

    public StatesDTO getStateById(int stateId, String token) {

        Long userId = null;
        if (tokenService.decodeToken(token.substring(7)).getSubject().equals("0")){
            userId=tokenService.decodeToken(token.substring(7)).getClaim("UnRegistered");
        }else
            userId=Long.parseLong(tokenService.decodeToken(token.substring(7)).getSubject());

        return jdbcClient.sql("""
                            SELECT s.state_id AS stateId, s.description, s.area, s.num_of_rooms AS numOfRooms ,s.garage_size AS garageSize,
                                               s.num_of_bath_rooms AS numOfBathRooms, s.num_of_storey AS numOfStorey,s.num_of_bed_rooms AS numOfBedrooms, l.value AS propertyType
                                               , l3.value AS propertySubType ,l2.value AS ownershipType
                               ,s.building_age AS buildingAge , s.price, s.longitude, s.latitude
                                        , s.published_at AS publishedAt, g.name_ar AS governorate , s.state_type AS category ,
                                          s.address , s.payment_method AS paymentMethod ,GROUP_CONCAT(DISTINCT CONCAT(:link, a.url_image) SEPARATOR  ',') AS attachments,
                                         GROUP_CONCAT(DISTINCT p.feature_code SEPARATOR  ',') AS features,
                                         CONCAT(z.first_name, ' ',z.last_name) AS publisherName,
                                         z.phone AS publisherPhone,
                                         count(distinct ua.id)  as viewCount,
                                        case when MAX(ua2.id) is not null then true else false end as isFavorite
                        FROM states s
                        left JOIN attachment a ON s.state_id = a.state_id
                        JOIN fnd_governorates g ON s.governorate = g.code
                        JOIN lookup l ON s.property_type = l.code AND l.type_code=1
                        JOIN lookup l2 ON s.ownership_type = l2.code AND l2.type_code=2
                        JOIN lookup l3 ON s.property_sub_type = l3.code AND l3.type_code=1
                        LEFT JOIN property_features p ON s.state_id = p.state_id
                        JOIN zone_users z ON s.created_user = z.user_id
                        LEFT JOIN user_actions ua ON ua.state_id = s.state_id AND ua.action_type = 'VIEW'
                        LEFT JOIN user_actions ua2 ON ua2.state_id = s.state_id AND ua2.action_type = 'FAVORITE' and ( ua2.app_user_id =:userId or ua2.unregistered_id =:userId)
                        WHERE s.is_active = 1  AND s.published_at IS NOT NULL AND s.state_id=:stateId
                          group by s.state_id, s.description, s.area, s.num_of_rooms,s.garage_size,
                                                   s.num_of_bath_rooms, s.num_of_storey,s.property_type,s.ownership_type, s.price, s.longitude, s.latitude, s.is_active
                                            ,s.created_user, s.published_at, g.name_ar , s.state_type, s.state_type ,s.address,s.payment_method,s.building_age ,l3.value
                        ORDER BY s.published_at DESC
                     """)
                .param("link", stateLink).param("userId",userId)
                .param("stateId",stateId).query(StatesDTO.class).single();
    }
}
