package com.hvati.administration.controller;

import com.hvati.administration.dto.CurrentUserDto;
import com.hvati.administration.entity.UserProfileEntity;
import com.hvati.administration.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/common/user")
@RequiredArgsConstructor
public class CurrentUserController {

    private final UserProfileRepository userProfileRepository;

    /**
     * Get current signed-in user from JWT token claims.
     */
    @GetMapping
    public ResponseEntity<CurrentUserDto> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        String name = jwt.getClaimAsString("name");
        String email = jwt.getClaimAsString("email");

        if (name == null) {
            name = jwt.getClaimAsString("preferred_username");
        }

        // Extract realm roles from token
        List<String> roles = extractRealmRoles(jwt);

        // Get avatar from local profile if available
        String avatar = null;
        UserProfileEntity profile = userProfileRepository.findByKeycloakId(userId).orElse(null);
        if (profile != null) {
            avatar = profile.getAvatarUrl();
        }

        CurrentUserDto user = CurrentUserDto.builder()
                .id(userId)
                .name(name)
                .email(email)
                .avatar(avatar)
                .status("online")
                .roles(roles)
                .build();

        return ResponseEntity.ok(user);
    }

    /**
     * Update current user profile.
     */
    @PatchMapping
    public ResponseEntity<CurrentUserDto> updateCurrentUser(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> body) {

        String userId = jwt.getSubject();

        // Get or create local profile
        UserProfileEntity profile = userProfileRepository.findByKeycloakId(userId)
                .orElseGet(() -> UserProfileEntity.builder().keycloakId(userId).build());

        @SuppressWarnings("unchecked")
        Map<String, Object> userData = (Map<String, Object>) body.get("user");
        if (userData != null) {
            if (userData.containsKey("avatar")) {
                profile.setAvatarUrl((String) userData.get("avatar"));
            }
        }

        userProfileRepository.save(profile);

        String name = jwt.getClaimAsString("name");
        String email = jwt.getClaimAsString("email");
        List<String> roles = extractRealmRoles(jwt);

        CurrentUserDto user = CurrentUserDto.builder()
                .id(userId)
                .name(name != null ? name : jwt.getClaimAsString("preferred_username"))
                .email(email)
                .avatar(profile.getAvatarUrl())
                .status("online")
                .roles(roles)
                .build();

        return ResponseEntity.ok(user);
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRealmRoles(Jwt jwt) {
        try {
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                List<String> allRoles = (List<String>) realmAccess.get("roles");
                return allRoles.stream()
                        .filter(r -> !r.startsWith("default-roles-")
                                && !r.startsWith("uma_")
                                && !r.equals("offline_access"))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            // ignore
        }
        return List.of();
    }
}
