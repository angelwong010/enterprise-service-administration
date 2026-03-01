package com.hvati.administration.controller;

import com.hvati.administration.dto.QuotationDto;
import com.hvati.administration.dto.SaleDto;
import com.hvati.administration.service.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/apps/quotations")
@RequiredArgsConstructor
public class QuotationsController {

    private final QuotationService quotationService;

    @GetMapping
    public ResponseEntity<List<QuotationDto>> getQuotations() {
        return ResponseEntity.ok(quotationService.getQuotations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuotationDto> getQuotationById(@PathVariable UUID id) {
        return quotationService.getQuotationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<QuotationDto> createQuotation(@RequestBody QuotationDto dto) {
        QuotationDto created = quotationService.createQuotation(dto);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/{id}/convert-to-sale")
    public ResponseEntity<SaleDto> convertToSale(@PathVariable UUID id) {
        return quotationService.convertQuotationToSale(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
