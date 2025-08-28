package com.ayn.states.realstate.repository.dashboard;

import com.ayn.states.realstate.entity.premission.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findAllByActive(boolean b);
}