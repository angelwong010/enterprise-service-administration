package com.hvati.administration.controller;

import com.hvati.administration.dto.ClientDto;
import com.hvati.administration.dto.PriceListDto;
import com.hvati.administration.dto.SaleSummaryDto;
import com.hvati.administration.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/apps/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients(
            @RequestParam(defaultValue = "false") boolean summary) {
        return ResponseEntity.ok(clientService.getAllClients(summary));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ClientDto>> searchClients(@RequestParam String q) {
        return ResponseEntity.ok(clientService.searchClients(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable UUID id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @GetMapping("/{id}/sales")
    public ResponseEntity<List<SaleSummaryDto>> getClientSales(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(clientService.getClientSales(id, Math.min(limit, 100)));
    }

    @PostMapping
    public ResponseEntity<ClientDto> createClient(@Valid @RequestBody ClientDto dto) {
        return ResponseEntity.ok(clientService.createClient(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(@PathVariable UUID id, @Valid @RequestBody ClientDto dto) {
        return ResponseEntity.ok(clientService.updateClient(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/price-lists")
    public ResponseEntity<List<PriceListDto>> getPriceLists() {
        return ResponseEntity.ok(clientService.getAllPriceLists());
    }
}
