package com.ayn.states.realstate.dto.attachment;

import com.ayn.states.realstate.entity.lookup.UrlImageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Attachments entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {

    private String urlImage;
    private UrlImageType urlImageType;
}
