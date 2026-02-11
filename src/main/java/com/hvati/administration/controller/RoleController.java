package com.hvati.administration.controller;

import com.hvati.administration.dto.PermissionDto;
import com.hvati.administration.dto.RoleDto;
import com.hvati.administration.dto.UpdateRoleRequest;
import com.hvati.administration.service.PermissionService;
import com.hvati.administration.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;

    // ---- Roles ----

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/roles/search")
    public ResponseEntity<List<RoleDto>> searchRoles(@RequestParam String query) {
        return ResponseEntity.ok(roleService.searchRoles(query));
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable String id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PostMapping("/roles")
    public ResponseEntity<RoleDto> createRole() {
        return ResponseEntity.ok(roleService.createRole());
    }

    @PatchMapping("/roles/{id}")
    public ResponseEntity<RoleDto> updateRole(@PathVariable String id, @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(id, request));
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Boolean> deleteRole(@PathVariable String id) {
        return ResponseEntity.ok(roleService.deleteRole(id));
    }

    // ---- Permissions ----

    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionDto>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }
}
