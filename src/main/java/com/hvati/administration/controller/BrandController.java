package com.hvati.administration.controller;

import com.hvati.administration.dto.BrandDto;
import com.hvati.administration.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/apps/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<List<BrandDto>> getAll() {
        return ResponseEntity.ok(brandService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<BrandDto>> search(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(brandService.search(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(brandService.getById(id));
    }

    @PostMapping
    public ResponseEntity<BrandDto> create(@Valid @RequestBody BrandDto dto) {
        return ResponseEntity.ok(brandService.create(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BrandDto> update(@PathVariable UUID id, @Valid @RequestBody BrandDto dto) {
        return ResponseEntity.ok(brandService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
