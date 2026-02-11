package com.hvati.administration.service;

import com.hvati.administration.dto.UserDto;
import com.hvati.administration.entity.TagEntity;
import com.hvati.administration.entity.UserProfileEntity;
import com.hvati.administration.mapper.UserMapper;
import com.hvati.administration.repository.TagRepository;
import com.hvati.administration.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloakUserService keycloakUserService;
    private final UserProfileRepository userProfileRepository;
    private final TagRepository tagRepository;
    private final UserMapper userMapper;

    /**
     * Get all users: fetches from Keycloak and merges with local DB profiles.
     */
    public List<UserDto> getAllUsers() {
        List<UserRepresentation> kcUsers = keycloakUserService.getAllUsers();

        // Fetch all local profiles indexed by keycloakId
        Map<String, UserProfileEntity> profileMap = userProfileRepository.findAll().stream()
                .collect(Collectors.toMap(UserProfileEntity::getKeycloakId, Function.identity()));

        return kcUsers.stream()
                .map(kcUser -> {
                    List<String> roles = keycloakUserService.getUserRealmRoles(kcUser.getId());
                    UserProfileEntity profile = profileMap.get(kcUser.getId());
                    return userMapper.toDto(kcUser, profile, roles);
                })
                .collect(Collectors.toList());
    }

    /**
     * Search users by query.
     */
    public List<UserDto> searchUsers(String query) {
        List<UserRepresentation> kcUsers = keycloakUserService.searchUsers(query);

        Map<String, UserProfileEntity> profileMap = userProfileRepository.findAll().stream()
                .collect(Collectors.toMap(UserProfileEntity::getKeycloakId, Function.identity()));

        return kcUsers.stream()
                .map(kcUser -> {
                    List<String> roles = keycloakUserService.getUserRealmRoles(kcUser.getId());
                    UserProfileEntity profile = profileMap.get(kcUser.getId());
                    return userMapper.toDto(kcUser, profile, roles);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get a user by ID.
     */
    public UserDto getUserById(String userId) {
        UserRepresentation kcUser = keycloakUserService.getUserById(userId);
        List<String> roles = keycloakUserService.getUserRealmRoles(userId);
        UserProfileEntity profile = userProfileRepository.findByKeycloakId(userId).orElse(null);
        return userMapper.toDto(kcUser, profile, roles);
    }

    /**
     * Create a new user in Keycloak + local profile in DB.
     */
    @Transactional
    public UserDto createUser() {
        // Create minimal user in Keycloak
        UserRepresentation newUser = new UserRepresentation();
        newUser.setEnabled(true);
        newUser.setUsername("new-user-" + System.currentTimeMillis());
        newUser.setFirstName("");
        newUser.setLastName("");

        String keycloakId = keycloakUserService.createUser(newUser);

        // Create local profile
        UserProfileEntity profile = UserProfileEntity.builder()
                .keycloakId(keycloakId)
                .build();
        userProfileRepository.save(profile);

        // Fetch the created user from Keycloak
        UserRepresentation createdUser = keycloakUserService.getUserById(keycloakId);
        return userMapper.toDto(createdUser, profile, Collections.emptyList());
    }

    /**
     * Update an existing user in Keycloak + local DB profile.
     */
    @Transactional
    public UserDto updateUser(String userId, UserDto userDto) {
        // Update Keycloak user
        UserRepresentation kcUser = keycloakUserService.getUserById(userId);
        if (userDto.getName() != null) {
            String[] parts = userDto.getName().split(" ", 2);
            kcUser.setFirstName(parts[0]);
            kcUser.setLastName(parts.length > 1 ? parts[1] : "");
        }
        if (userDto.getEmails() != null && !userDto.getEmails().isEmpty()) {
            kcUser.setEmail(userDto.getEmails().get(0).getEmail());
        }
        keycloakUserService.updateUser(userId, kcUser);

        // Update roles if provided
        if (userDto.getRoles() != null) {
            keycloakUserService.assignRealmRoles(userId, userDto.getRoles());
        }

        // Update local profile
        UserProfileEntity profile = userProfileRepository.findByKeycloakId(userId)
                .orElseGet(() -> UserProfileEntity.builder().keycloakId(userId).build());

        profile.setTitle(userDto.getTitle());
        profile.setCompany(userDto.getCompany());
        profile.setBirthday(userDto.getBirthday() != null ? LocalDate.parse(userDto.getBirthday()) : null);
        profile.setAddress(userDto.getAddress());
        profile.setNotes(userDto.getNotes());
        profile.setAvatarUrl(userDto.getAvatar());
        profile.setBackgroundUrl(userDto.getBackground());

        // Update tags
        if (userDto.getTags() != null) {
            Set<TagEntity> tags = userDto.getTags().stream()
                    .map(tagId -> tagRepository.findById(UUID.fromString(tagId)).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            profile.setTags(tags);
        }

        userProfileRepository.save(profile);

        // Return updated user
        List<String> roles = keycloakUserService.getUserRealmRoles(userId);
        UserRepresentation updatedKcUser = keycloakUserService.getUserById(userId);
        return userMapper.toDto(updatedKcUser, profile, roles);
    }

    /**
     * Delete a user from Keycloak and local DB.
     */
    @Transactional
    public boolean deleteUser(String userId) {
        try {
            keycloakUserService.deleteUser(userId);
            userProfileRepository.findByKeycloakId(userId)
                    .ifPresent(userProfileRepository::delete);
            return true;
        } catch (Exception e) {
            log.error("Error deleting user {}: {}", userId, e.getMessage());
            return false;
        }
    }
}
