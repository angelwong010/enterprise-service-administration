package com.hvati.administration.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakUserService {

    private final RealmResource realmResource;

    /**
     * Get all users from Keycloak.
     */
    public List<UserRepresentation> getAllUsers() {
        return realmResource.users().list(0, Integer.MAX_VALUE);
    }

    /**
     * Search users by keyword (name, email, username).
     */
    public List<UserRepresentation> searchUsers(String query) {
        return realmResource.users().search(query, 0, Integer.MAX_VALUE);
    }

    /**
     * Get a single user by ID.
     */
    public UserRepresentation getUserById(String userId) {
        return realmResource.users().get(userId).toRepresentation();
    }

    /**
     * Create a new user in Keycloak.
     * Returns the user ID from the Location header.
     */
    public String createUser(UserRepresentation user) {
        try (Response response = realmResource.users().create(user)) {
            if (response.getStatus() == 201) {
                String locationPath = response.getLocation().getPath();
                return locationPath.substring(locationPath.lastIndexOf('/') + 1);
            }
            throw new RuntimeException("Failed to create user in Keycloak. Status: " + response.getStatus());
        }
    }

    /**
     * Update an existing user in Keycloak.
     */
    public void updateUser(String userId, UserRepresentation user) {
        realmResource.users().get(userId).update(user);
    }

    /**
     * Delete a user from Keycloak.
     */
    public void deleteUser(String userId) {
        try (Response response = realmResource.users().delete(userId)) {
            if (response.getStatus() != 204) {
                throw new RuntimeException("Failed to delete user in Keycloak. Status: " + response.getStatus());
            }
        }
    }

    /**
     * Get realm roles assigned to a user.
     */
    public List<String> getUserRealmRoles(String userId) {
        try {
            UserResource userResource = realmResource.users().get(userId);
            List<RoleRepresentation> roles = userResource.roles().realmLevel().listEffective();
            return roles.stream()
                    .map(RoleRepresentation::getName)
                    .filter(name -> !name.startsWith("default-roles-") && !name.startsWith("uma_") && !name.equals("offline_access"))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Could not fetch roles for user {}: {}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Assign realm roles to a user.
     */
    public void assignRealmRoles(String userId, List<String> roleNames) {
        UserResource userResource = realmResource.users().get(userId);

        // First remove all current realm roles (except default ones)
        List<RoleRepresentation> currentRoles = userResource.roles().realmLevel().listEffective();
        List<RoleRepresentation> toRemove = currentRoles.stream()
                .filter(r -> !r.getName().startsWith("default-roles-") && !r.getName().startsWith("uma_") && !r.getName().equals("offline_access"))
                .collect(Collectors.toList());
        if (!toRemove.isEmpty()) {
            userResource.roles().realmLevel().remove(toRemove);
        }

        // Assign new roles
        if (roleNames != null && !roleNames.isEmpty()) {
            List<RoleRepresentation> rolesToAdd = roleNames.stream()
                    .map(name -> realmResource.roles().get(name).toRepresentation())
                    .collect(Collectors.toList());
            userResource.roles().realmLevel().add(rolesToAdd);
        }
    }
}
