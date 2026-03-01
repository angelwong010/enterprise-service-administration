package com.hvati.administration.controller;

import com.hvati.administration.dto.SaleDto;
import com.hvati.administration.service.SaleAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/apps/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SaleAppService saleAppService;

    @GetMapping
    public ResponseEntity<List<SaleDto>> getSales(
            @RequestParam(required = false) Integer number) {
        if (number != null) {
            return saleAppService.getSaleByNumber(number)
                    .map(List::of)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.ok(List.of()));
        }
        return ResponseEntity.ok(saleAppService.getSales());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleDto> getSaleById(@PathVariable UUID id) {
        return saleAppService.getSaleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SaleDto> createSale(@RequestBody SaleDto dto) {
        SaleDto created = saleAppService.createSale(dto);
        return ResponseEntity.ok(created);
    }
}
