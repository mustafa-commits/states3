package com.ayn.states.realstate.repository.attachment;

import com.ayn.states.realstate.entity.att.Attachments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentsRepo extends JpaRepository<Attachments,Long> {


    @Query("""
        SELECT true FROM Attachments a WHERE a.UrlImage= :image""")
    boolean getAttachmentsByUrlImageExists(@Param("image") String urlImage);
}
