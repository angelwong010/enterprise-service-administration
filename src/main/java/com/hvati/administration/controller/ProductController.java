package com.hvati.administration.controller;

import com.hvati.administration.dto.ProductDto;
import com.hvati.administration.dto.StockMovementDto;
import com.hvati.administration.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/apps/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAll(
            @RequestParam(defaultValue = "true") boolean pricesAndStock) {
        return ResponseEntity.ok(productService.getAll(pricesAndStock));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "true") boolean pricesAndStock) {
        return ResponseEntity.ok(productService.search(q, pricesAndStock));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping("/{id}/movements")
    public ResponseEntity<List<StockMovementDto>> getMovements(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(productService.getLastMovements(id, Math.min(limit, 100)));
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.create(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable UUID id, @Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
