package com.hvati.administration.controller;

import com.hvati.administration.dto.TagDto;
import com.hvati.administration.dto.UpdateUserRequest;
import com.hvati.administration.dto.UserDto;
import com.hvati.administration.service.TagService;
import com.hvati.administration.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/apps/contacts")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TagService tagService;

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
    public ResponseEntity<List<Object>> getCountries() {
        // Return empty list for now; can be populated with static JSON
        return ResponseEntity.ok(List.of());
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
