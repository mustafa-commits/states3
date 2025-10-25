package com.ayn.states.realstate.repository.dashboard;

import com.ayn.states.realstate.entity.premission.PermissionGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, Long> {

    @EntityGraph(attributePaths = {"permissions"})
    List<PermissionGroup> findAll();

    List<PermissionGroup> findAllByActive(boolean active);

    Optional<PermissionGroup> findByIdAndActiveTrue(Long id);
}