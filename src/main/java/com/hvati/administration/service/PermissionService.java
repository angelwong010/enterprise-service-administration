package com.hvati.administration.service;

import com.hvati.administration.dto.PermissionDto;
import com.hvati.administration.entity.PermissionEntity;
import com.hvati.administration.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    /**
     * Get all permissions from the database.
     */
    public List<PermissionDto> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private PermissionDto toDto(PermissionEntity entity) {
        return PermissionDto.builder()
                .id(entity.getId())
                .label(entity.getLabel())
                .category(entity.getCategory())
                .build();
    }
}
