package com.hvati.administration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakRoleService {

    private final RealmResource realmResource;

    /**
     * Get all realm-level roles (excluding default/internal ones).
     */
    public List<RoleRepresentation> getAllRoles() {
        return realmResource.roles().list().stream()
                .filter(r -> !r.getName().startsWith("default-roles-")
                        && !r.getName().startsWith("uma_")
                        && !r.getName().equals("offline_access"))
                .toList();
    }

    /**
     * Get a role by its ID.
     */
    public RoleRepresentation getRoleById(String roleId) {
        return realmResource.rolesById().getRole(roleId);
    }

    /**
     * Get a role by its name.
     */
    public RoleRepresentation getRoleByName(String roleName) {
        return realmResource.roles().get(roleName).toRepresentation();
    }

    /**
     * Create a new realm role in Keycloak.
     */
    public RoleRepresentation createRole(String name, String description) {
        RoleRepresentation role = new RoleRepresentation();
        role.setName(name);
        role.setDescription(description);
        realmResource.roles().create(role);

        // Fetch and return the created role
        return realmResource.roles().get(name).toRepresentation();
    }

    /**
     * Update an existing realm role in Keycloak.
     */
    public void updateRole(String roleId, String name, String description, boolean isActive) {
        RoleRepresentation role = realmResource.rolesById().getRole(roleId);
        role.setName(name);
        role.setDescription(description);

        // Use attributes to store active/inactive state
        Map<String, List<String>> attributes = role.getAttributes();
        if (attributes == null) {
            attributes = new java.util.HashMap<>();
        }
        attributes.put("inactive", List.of(String.valueOf(!isActive)));
        role.setAttributes(attributes);

        realmResource.rolesById().updateRole(roleId, role);
    }

    /**
     * Delete a realm role from Keycloak.
     */
    public void deleteRole(String roleId) {
        realmResource.rolesById().deleteRole(roleId);
    }

    /**
     * Get count of users assigned to a role.
     */
    public int getUsersCountForRole(String roleName) {
        try {
            List<UserRepresentation> users = realmResource.roles().get(roleName).getUserMembers(0, Integer.MAX_VALUE);
            return users != null ? users.size() : 0;
        } catch (Exception e) {
            log.warn("Could not fetch users count for role {}: {}", roleName, e.getMessage());
            return 0;
        }
    }

    /**
     * Search roles by query filtering by name or description.
     */
    public List<RoleRepresentation> searchRoles(String query) {
        String lowerQuery = query.toLowerCase();
        return getAllRoles().stream()
                .filter(r -> {
                    String name = r.getName() != null ? r.getName().toLowerCase() : "";
                    String desc = r.getDescription() != null ? r.getDescription().toLowerCase() : "";
                    return name.contains(lowerQuery) || desc.contains(lowerQuery);
                })
                .toList();
    }
}
