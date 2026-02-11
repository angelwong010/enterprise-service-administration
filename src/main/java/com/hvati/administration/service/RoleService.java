package com.hvati.administration.service;

import com.hvati.administration.dto.RoleDto;
import com.hvati.administration.dto.UpdateRoleRequest;
import com.hvati.administration.entity.PermissionEntity;
import com.hvati.administration.entity.RolePermissionEntity;
import com.hvati.administration.mapper.RoleMapper;
import com.hvati.administration.repository.PermissionRepository;
import com.hvati.administration.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final KeycloakRoleService keycloakRoleService;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    /**
     * Get all roles: Keycloak realm roles + DB permission mappings merged.
     */
    public List<RoleDto> getAllRoles() {
        List<RoleRepresentation> kcRoles = keycloakRoleService.getAllRoles();

        // Get all role-permission mappings from DB
        List<String> roleNames = kcRoles.stream().map(RoleRepresentation::getName).toList();
        Map<String, List<RolePermissionEntity>> permissionsByRole = rolePermissionRepository
                .findByRoleNameIn(roleNames).stream()
                .collect(Collectors.groupingBy(RolePermissionEntity::getRoleName));

        return kcRoles.stream()
                .map(kcRole -> {
                    int usersCount = keycloakRoleService.getUsersCountForRole(kcRole.getName());
                    List<RolePermissionEntity> perms = permissionsByRole.getOrDefault(kcRole.getName(), Collections.emptyList());
                    return roleMapper.toDto(kcRole, perms, usersCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * Search roles by query.
     */
    public List<RoleDto> searchRoles(String query) {
        List<RoleRepresentation> kcRoles = keycloakRoleService.searchRoles(query);

        List<String> roleNames = kcRoles.stream().map(RoleRepresentation::getName).toList();
        Map<String, List<RolePermissionEntity>> permissionsByRole = rolePermissionRepository
                .findByRoleNameIn(roleNames).stream()
                .collect(Collectors.groupingBy(RolePermissionEntity::getRoleName));

        return kcRoles.stream()
                .map(kcRole -> {
                    int usersCount = keycloakRoleService.getUsersCountForRole(kcRole.getName());
                    List<RolePermissionEntity> perms = permissionsByRole.getOrDefault(kcRole.getName(), Collections.emptyList());
                    return roleMapper.toDto(kcRole, perms, usersCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get a role by ID.
     */
    public RoleDto getRoleById(String roleId) {
        RoleRepresentation kcRole = keycloakRoleService.getRoleById(roleId);
        int usersCount = keycloakRoleService.getUsersCountForRole(kcRole.getName());
        List<RolePermissionEntity> perms = rolePermissionRepository.findByRoleName(kcRole.getName());
        return roleMapper.toDto(kcRole, perms, usersCount);
    }

    /**
     * Create a new role in Keycloak.
     */
    public RoleDto createRole() {
        String roleName = "new-role-" + System.currentTimeMillis();
        RoleRepresentation kcRole = keycloakRoleService.createRole(roleName, "");
        return roleMapper.toDto(kcRole, Collections.emptyList(), 0);
    }

    /**
     * Update a role in Keycloak + update permission mappings in DB.
     */
    @Transactional
    public RoleDto updateRole(String roleId, UpdateRoleRequest request) {
        // Get current role to know the old name
        RoleRepresentation currentRole = keycloakRoleService.getRoleById(roleId);
        String oldRoleName = currentRole.getName();

        // Update in Keycloak
        keycloakRoleService.updateRole(roleId, request.getName(), request.getDescription(), request.isActive());

        // Update permissions in DB
        rolePermissionRepository.deleteByRoleName(oldRoleName);

        if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
            String newRoleName = request.getName() != null ? request.getName() : oldRoleName;
            List<RolePermissionEntity> newPermissions = request.getPermissions().stream()
                    .map(permId -> {
                        PermissionEntity perm = permissionRepository.findById(permId).orElse(null);
                        if (perm == null) return null;
                        return RolePermissionEntity.builder()
                                .roleName(newRoleName)
                                .permission(perm)
                                .build();
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
            rolePermissionRepository.saveAll(newPermissions);
        }

        // Return updated role
        RoleRepresentation updatedRole = keycloakRoleService.getRoleById(roleId);
        int usersCount = keycloakRoleService.getUsersCountForRole(updatedRole.getName());
        List<RolePermissionEntity> perms = rolePermissionRepository.findByRoleName(updatedRole.getName());
        return roleMapper.toDto(updatedRole, perms, usersCount);
    }

    /**
     * Delete a role from Keycloak + remove permission mappings from DB.
     */
    @Transactional
    public boolean deleteRole(String roleId) {
        try {
            RoleRepresentation role = keycloakRoleService.getRoleById(roleId);
            rolePermissionRepository.deleteByRoleName(role.getName());
            keycloakRoleService.deleteRole(roleId);
            return true;
        } catch (Exception e) {
            log.error("Error deleting role {}: {}", roleId, e.getMessage());
            return false;
        }
    }
}
