package com.hvati.administration.mapper;

import com.hvati.administration.dto.RoleDto;
import com.hvati.administration.entity.RolePermissionEntity;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    /**
     * Converts a Keycloak RoleRepresentation + DB permissions into a RoleDto.
     */
    public RoleDto toDto(RoleRepresentation kcRole, List<RolePermissionEntity> rolePermissions, int usersCount) {
        List<String> permissionIds = rolePermissions != null
                ? rolePermissions.stream()
                    .map(rp -> rp.getPermission().getId())
                    .collect(Collectors.toList())
                : Collections.emptyList();

        return RoleDto.builder()
                .id(kcRole.getId())
                .name(kcRole.getName())
                .description(kcRole.getDescription() != null ? kcRole.getDescription() : "")
                .usersCount(usersCount)
                .isActive(!isCompositeInactive(kcRole))
                .updatedAt(Instant.now().toString())
                .permissions(permissionIds)
                .build();
    }

    /**
     * Determines if a role should be considered inactive.
     * Keycloak roles don't have an "active" concept, so we use attributes or naming convention.
     */
    private boolean isCompositeInactive(RoleRepresentation role) {
        if (role.getAttributes() != null && role.getAttributes().containsKey("inactive")) {
            List<String> values = role.getAttributes().get("inactive");
            return values != null && !values.isEmpty() && "true".equalsIgnoreCase(values.get(0));
        }
        return false;
    }
}
