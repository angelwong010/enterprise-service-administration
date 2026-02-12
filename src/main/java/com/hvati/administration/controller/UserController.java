package com.hvati.administration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hvati.administration.dto.TagDto;
import com.hvati.administration.dto.UpdateUserRequest;
import com.hvati.administration.dto.UserDto;
import com.hvati.administration.service.TagService;
import com.hvati.administration.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/apps/contacts")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TagService tagService;
    private final ObjectMapper objectMapper;

    private List<Map<String, String>> countriesCache = Collections.emptyList();

    @PostConstruct
    public void loadCountries() {
        try {
            InputStream is = new ClassPathResource("static/countries.json").getInputStream();
            countriesCache = objectMapper.readValue(is, new TypeReference<>() {});
            log.info("Loaded {} countries from static resource", countriesCache.size());
        } catch (Exception e) {
            log.warn("Could not load countries.json: {}", e.getMessage());
        }
    }

    // ---- Users ----

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }

    @PostMapping("/contact")
    public ResponseEntity<UserDto> createUser() {
        return ResponseEntity.ok(userService.createUser());
    }

    @PatchMapping("/contact")
    public ResponseEntity<UserDto> updateUser(@RequestBody UpdateUserRequest request) {
        log.info("PATCH /contact received - id='{}', contact.id='{}'",
                request.getId(),
                request.getContact() != null ? request.getContact().getId() : "null");
        return ResponseEntity.ok(userService.updateUser(request.getId(), request.getContact()));
    }

    @DeleteMapping("/contact")
    public ResponseEntity<Boolean> deleteUser(@RequestParam String id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    @PostMapping("/avatar")
    public ResponseEntity<UserDto> uploadAvatar(@RequestBody Map<String, Object> body) {
        String id = (String) body.get("id");
        // For now, just return the user. File upload handling can be added later.
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // ---- Countries (static data) ----

    @GetMapping("/countries")
    public ResponseEntity<List<Map<String, String>>> getCountries() {
        return ResponseEntity.ok(countriesCache);
    }

    // ---- Tags ----

    @GetMapping("/tags")
    public ResponseEntity<List<TagDto>> getTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @PostMapping("/tag")
    public ResponseEntity<TagDto> createTag(@RequestBody Map<String, TagDto> body) {
        return ResponseEntity.ok(tagService.createTag(body.get("tag")));
    }

    @PatchMapping("/tag")
    public ResponseEntity<TagDto> updateTag(@RequestBody Map<String, Object> body) {
        String id = (String) body.get("id");
        @SuppressWarnings("unchecked")
        Map<String, String> tagMap = (Map<String, String>) body.get("tag");
        TagDto tagDto = TagDto.builder().title(tagMap.get("title")).build();
        return ResponseEntity.ok(tagService.updateTag(id, tagDto));
    }

    @DeleteMapping("/tag")
    public ResponseEntity<Boolean> deleteTag(@RequestParam String id) {
        return ResponseEntity.ok(tagService.deleteTag(id));
    }
}
