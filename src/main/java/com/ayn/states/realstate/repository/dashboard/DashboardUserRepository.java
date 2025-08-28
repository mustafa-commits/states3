package com.ayn.states.realstate.repository.dashboard;

import com.ayn.states.realstate.entity.dashboard.DashboardUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardUserRepository extends JpaRepository<DashboardUser, Long> {

    @EntityGraph(attributePaths = {"permissionGroup", "permissionGroup.permissions"})
    Optional<DashboardUser> findByUsername(String username);

    @EntityGraph(attributePaths = {"permissionGroup", "permissionGroup.permissions"})
    List<DashboardUser> findAll();

}