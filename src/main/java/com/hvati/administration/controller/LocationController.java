package com.hvati.administration.controller;

import com.hvati.administration.dto.LocationDto;
import com.hvati.administration.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/apps/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<List<LocationDto>> getAll() {
        return ResponseEntity.ok(locationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(locationService.getById(id));
    }
}
