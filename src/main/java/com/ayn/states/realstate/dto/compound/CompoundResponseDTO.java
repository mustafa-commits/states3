package com.ayn.states.realstate.dto.compound;

import com.ayn.states.realstate.entity.compound.CompoundPost;
import com.ayn.states.realstate.entity.compound.SocialLinks;
import com.ayn.states.realstate.entity.compound.UnitMap;

import java.util.List;

public record CompoundResponseDTO(
        Long id,
        String coverImageUrl,
        String name,
        int followersCount,
        String thumbnailUrl,
        String address,
        SocialLinks socialLinks,
        int viewsCount,
        int propertiesCount,
        String description,
        List<String> features,
        List<CompoundPost> posts,
        List<UnitMap> unitMaps,
        String model3dUrl,
        Double latitude,
        Double longitude,
        String contactNumber
) {

    // Custom constructor for JDBC mapping (without collections)
    public CompoundResponseDTO(Long id, String coverImageUrl, String name, int followersCount,
                               String thumbnailUrl, String address, SocialLinks socialLinks,
                               int viewsCount, int propertiesCount, String description,
                               String model3dUrl, Double latitude, Double longitude, String contactNumber) {
        this(id, coverImageUrl, name, followersCount, thumbnailUrl, address, socialLinks,
                viewsCount, propertiesCount, description, List.of(), List.of(), List.of(),
                model3dUrl, latitude, longitude, contactNumber);
    }
    // Builder-like methods for immutable updates
    public CompoundResponseDTO withFeatures(List<String> features) {
        return new CompoundResponseDTO(id, coverImageUrl, name, followersCount, thumbnailUrl,
                address, socialLinks, viewsCount, propertiesCount, description,
                features, posts, unitMaps, model3dUrl, latitude, longitude, contactNumber);
    }

    public CompoundResponseDTO withPosts(List<CompoundPost> posts) {
        return new CompoundResponseDTO(id, coverImageUrl, name, followersCount, thumbnailUrl,
                address, socialLinks, viewsCount, propertiesCount, description,
                features, posts, unitMaps, model3dUrl, latitude, longitude, contactNumber);
    }

    public CompoundResponseDTO withUnitMaps(List<UnitMap> unitMaps) {
        return new CompoundResponseDTO(id, coverImageUrl, name, followersCount, thumbnailUrl,
                address, socialLinks, viewsCount, propertiesCount, description,
                features, posts, unitMaps, model3dUrl, latitude, longitude, contactNumber);
    }
}
