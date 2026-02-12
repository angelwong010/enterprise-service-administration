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
     * Generates a temporary password that the user must change on first login.
     */
    @Transactional
    public UserDto createUser() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String tempPassword = "Tmp-" + UUID.randomUUID().toString().substring(0, 12);

        UserRepresentation newUser = new UserRepresentation();
        newUser.setEnabled(true);
        newUser.setUsername("user-" + uniqueId);
        newUser.setFirstName("Nuevo");
        newUser.setLastName("Usuario");
        newUser.setEmail("placeholder-" + uniqueId + "@temp.local");
        newUser.setEmailVerified(false);

        String keycloakId = keycloakUserService.createUser(newUser);

        // Set temporary password (user must change on first login)
        keycloakUserService.setTemporaryPassword(keycloakId, tempPassword);

        // Create local profile
        UserProfileEntity profile = UserProfileEntity.builder()
                .keycloakId(keycloakId)
                .build();
        userProfileRepository.save(profile);

        // Fetch the created user and include temporary password in response
        UserRepresentation createdUser = keycloakUserService.getUserById(keycloakId);
        UserDto dto = userMapper.toDto(createdUser, profile, Collections.emptyList());
        dto.setTemporaryPassword(tempPassword);
        return dto;
    }

    /**
     * Update an existing user in Keycloak + local DB profile.
     */
    @Transactional
    public UserDto updateUser(String userId, UserDto userDto) {
        log.info("updateUser called with userId='{}', userDto.id='{}'", userId, userDto != null ? userDto.getId() : "null");

        // If userId is null/empty but userDto has an id, use that
        if ((userId == null || userId.isBlank()) && userDto != null && userDto.getId() != null && !userDto.getId().isBlank()) {
            userId = userDto.getId();
            log.info("Using userDto.id as fallback: '{}'", userId);
        }

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

        // Update roles if provided (not null)
        if (userDto.getRoles() != null) {
            log.info("Assigning roles to user {}: {}", userId, userDto.getRoles());
            keycloakUserService.assignRealmRoles(userId, userDto.getRoles());
        }

        // Update local profile
        String finalUserId = userId;
        UserProfileEntity profile = userProfileRepository.findByKeycloakId(userId)
                .orElseGet(() -> UserProfileEntity.builder().keycloakId(finalUserId).build());

        profile.setTitle(userDto.getTitle());
        profile.setCompany(userDto.getCompany());
        profile.setBirthday(parseBirthday(userDto.getBirthday()));
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

    /**
     * Parse birthday string from frontend.
     * Accepts ISO datetime (e.g. "1956-11-22T00:00:00.000-06:00") or plain date ("1956-11-22").
     */
    private LocalDate parseBirthday(String birthday) {
        if (birthday == null || birthday.isBlank()) {
            return null;
        }
        try {
            // If it contains 'T', it's an ISO datetime — extract just the date part
            if (birthday.contains("T")) {
                return LocalDate.parse(birthday.substring(0, birthday.indexOf('T')));
            }
            return LocalDate.parse(birthday);
        } catch (Exception e) {
            log.warn("Could not parse birthday '{}': {}", birthday, e.getMessage());
            return null;
        }
    }
}
