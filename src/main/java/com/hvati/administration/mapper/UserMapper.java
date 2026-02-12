package com.hvati.administration.mapper;

import com.hvati.administration.dto.UserDto;
import com.hvati.administration.entity.UserProfileEntity;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    /**
     * Merges Keycloak user data with local DB profile data into a unified UserDto.
     */
    public UserDto toDto(UserRepresentation kcUser, UserProfileEntity profile, List<String> roles) {
        UserDto.UserDtoBuilder builder = UserDto.builder()
                .id(kcUser.getId())
                .name(buildName(kcUser))
                .roles(roles != null ? roles : Collections.emptyList())
                .tags(Collections.emptyList())
                .emails(Collections.emptyList())
                .phoneNumbers(Collections.emptyList());

        // Email from Keycloak
        if (kcUser.getEmail() != null) {
            builder.emails(List.of(
                    UserDto.EmailEntry.builder()
                            .email(kcUser.getEmail())
                            .label("personal")
                            .build()
            ));
        }

        // Merge with DB profile if available
        if (profile != null) {
            builder.title(profile.getTitle())
                    .company(profile.getCompany())
                    .birthday(profile.getBirthday() != null ? profile.getBirthday().toString() : null)
                    .address(profile.getAddress())
                    .notes(profile.getNotes())
                    .avatar(profile.getAvatarUrl())
                    .background(profile.getBackgroundUrl());

            if (profile.getTags() != null && !profile.getTags().isEmpty()) {
                builder.tags(profile.getTags().stream()
                        .map(tag -> tag.getId().toString())
                        .collect(Collectors.toList()));
            }
        }

        return builder.build();
    }

    /**
     * Merges Keycloak user data without a DB profile.
     */
    public UserDto toDto(UserRepresentation kcUser, List<String> roles) {
        return toDto(kcUser, null, roles);
    }

    private String buildName(UserRepresentation kcUser) {
        String firstName = kcUser.getFirstName() != null ? kcUser.getFirstName() : "";
        String lastName = kcUser.getLastName() != null ? kcUser.getLastName() : "";
        String fullName = (firstName + " " + lastName).trim();
        return fullName.isEmpty() ? kcUser.getUsername() : fullName;
    }
}
