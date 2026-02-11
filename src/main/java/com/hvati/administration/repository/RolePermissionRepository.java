package com.hvati.administration.repository;

import com.hvati.administration.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, UUID> {

    List<RolePermissionEntity> findByRoleName(String roleName);

    List<RolePermissionEntity> findByRoleNameIn(List<String> roleNames);

    void deleteByRoleName(String roleName);
}
