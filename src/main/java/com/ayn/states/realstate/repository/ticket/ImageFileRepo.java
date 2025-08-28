package com.ayn.states.realstate.repository.ticket;

import com.ayn.states.realstate.entity.ticket.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageFileRepo extends JpaRepository<ImageFile,Integer> {
    Optional<ImageFile> findFirstByName(String fileName);
}
