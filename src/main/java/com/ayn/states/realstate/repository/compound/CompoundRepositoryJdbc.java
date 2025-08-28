package com.ayn.states.realstate.repository.compound;

import com.ayn.states.realstate.dto.compound.*;
import com.ayn.states.realstate.entity.compound.CompoundPost;
import com.ayn.states.realstate.entity.compound.SocialLinks;
import com.ayn.states.realstate.entity.compound.UnitMap;
import com.ayn.states.realstate.entity.states.States;
import com.ayn.states.realstate.exception.UnauthorizedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class CompoundRepositoryJdbc {

    @Autowired
    private JdbcClient jdbcClient;

    @Value("${COMPOUND_BASE}")
    private String baseUrl;


    public List<CompoundImageDto> findActiveCompoundImages() {

        return jdbcClient.sql("""
            SELECT
                id,
                CONCAT(?, cover_image_url) AS coverImageUrl
            FROM compounds c
            WHERE c.is_active = 1 AND c.approved_at IS NOT NULL
            """)
                .param(baseUrl)
                .query(CompoundImageDto.class)
                .list();
    }




    public CompoundResponseDTO findCompoundById(Long id) throws JsonProcessingException {
        String sql = """
    SELECT
                      c.id,
                      CONCAT(:baseUrl, c.cover_image_url) AS coverImageUrl,
                      c.name,
                      c.followers_count AS followersCount,
                      c.thumbnail_url AS thumbnailUrl,
                      c.address,
                      JSON_OBJECT(
                          'facebook', c.facebook,
                          'instagram', c.instagram,
                          'twitter', c.twitter
                      ) AS socialLinks,
                      c.views_count AS viewsCount,
                      c.properties_count AS propertiesCount,
                      c.description,
                      CONCAT(:baseUrl, c.model3d_url) AS model3dUrl,
                      c.latitude,
                      c.longitude,
                      c.contact_number AS contactNumber,
                  
                      -- FIXED: Use CASE to return NULL if no feature exists
                      COALESCE(JSON_ARRAYAGG(
                          CASE WHEN cf.feature IS NOT NULL THEN cf.feature ELSE NULL END
                      ), JSON_ARRAY()) AS features,
                  
                      -- FIXED: Use CASE to return NULL if no post exists
        CASE WHEN cp.id IS NOT NULL THEN
                      COALESCE(JSON_ARRAYAGG(
                          
                              JSON_OBJECT(
                                  'id', cp.id,
                                  'title', cp.title,
                                  'imageUrl', CONCAT(:baseUrl, cp.image_url),
                                  'content', cp.content
                              )
                          
                      ), JSON_ARRAY()) ELSE NULL END AS posts,
                  
                      -- FIXED: Use CASE to return NULL if no unit map exists
        CASE WHEN um.id IS NOT NULL THEN
                      COALESCE(JSON_ARRAYAGG(
                              JSON_OBJECT(
                                  'id', um.id,
                                  'description', um.description,
                                  'imageUrl', CONCAT(:baseUrl, um.image_url)
                              )
                      ), JSON_ARRAY()) ELSE NULL END AS unitMaps
                  
                  FROM
                      compounds c
                  LEFT JOIN
                      compound_features cf ON cf.compound_id = c.id
                  LEFT JOIN
                      compound_posts cp ON cp.compound_id = c.id AND cp.approved_at IS NOT NULL
                  LEFT JOIN
                      unit_maps um ON um.compound_id = c.id
                  WHERE
                      c.id = :id
                      AND c.is_active = 1
                      AND c.approved_at IS NOT NULL
                  GROUP BY
                      c.id
                  LIMIT 1
""";

        return jdbcClient.sql(sql)
                .param("id", id)
                .param("baseUrl", baseUrl)
                .query(rs -> {
                    if (!rs.next()) {
                        return null; // No result found
                    }

                    ObjectMapper mapper = new ObjectMapper();

                    try {
                        SocialLinks socialLinks = mapper.readValue(rs.getString("socialLinks"), SocialLinks.class);
                        List<String> features = mapper.readValue(rs.getString("features"), new TypeReference<List<String>>() {});
                        List<CompoundPost> posts = mapper.readValue(rs.getString("posts"), new TypeReference<List<CompoundPost>>() {});
                        List<UnitMap> unitMaps = mapper.readValue(rs.getString("unitMaps"), new TypeReference<List<UnitMap>>() {});

                        return new CompoundResponseDTO(
                                rs.getLong("id"),
                                rs.getString("coverImageUrl"),
                                rs.getString("name"),
                                rs.getInt("followersCount"),
                                rs.getString("thumbnailUrl"),
                                rs.getString("address"),
                                socialLinks,
                                rs.getInt("viewsCount"),
                                rs.getInt("propertiesCount"),
                                rs.getString("description"),
                                features,
                                posts,
                                unitMaps,
                                rs.getString("model3dUrl"),
                                rs.getDouble("latitude"),
                                rs.getDouble("longitude"),
                                rs.getString("contactNumber")
                        );
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error parsing JSON data", e);
                    }
                });
    }



    public List<CompoundDetailsDto> unPublishedCompounds(Long page) {
        CompoundBaseDto base = jdbcClient.sql("""
            SELECT
                c.id,
                CONCAT(:baseUrl, c.cover_image_url) AS coverImageUrl,
                c.name,
                c.followers_count AS followersCount,
                CONCAT(:baseUrl, c.thumbnail_url) AS thumbnailUrl,
                c.address,
                c.facebook,
                c.instagram,
                c.twitter,
                c.views_count AS viewsCount,
                c.properties_count AS propertiesCount,
                c.description,
                CONCAT(:baseUrl, c.model3d_url) AS model3dUrl,
                c.latitude,
                c.longitude,
                c.contact_number AS contactNumber
            FROM compounds c
            WHERE c.id = :id AND c.is_active = 1 AND c.approved_at IS NULL
            LIMIT 1
        """)
                .param("baseUrl",baseUrl)
                .query(CompoundBaseDto.class)
                .optional()
                .orElse(null);

        // Features
        List<String> features = jdbcClient.sql("""
                SELECT feature 
                FROM compound_features 
                WHERE compound_id = ?
            """)
                .query(String.class)
                .list();

        // Posts
        List<CompoundPostDto> posts = jdbcClient.sql("""
                SELECT id, title, CONCAT(?, image_url) AS imageUrl, content
                FROM compound_posts
                WHERE compound_id = ?
            """)
                .param(baseUrl)
                .query(CompoundPostDto.class)
                .list();

        // Unit maps
        List<UnitMapDto2> unitMaps = jdbcClient.sql("""
                SELECT id, description, CONCAT(?, image_url) AS imageUrl
                FROM unit_maps
                WHERE compound_id = ?
            """)
                .param(baseUrl)
                .query(UnitMapDto2.class)
                .list();

        // Return combined DTO
        return Collections.singletonList(new CompoundDetailsDto(base, features, posts, unitMaps));
    }




}

