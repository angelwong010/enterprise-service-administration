package com.hvati.administration.service;

import com.hvati.administration.dto.BrandDto;
import com.hvati.administration.entity.BrandEntity;
import com.hvati.administration.mapper.ProductMapper;
import com.hvati.administration.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    public List<BrandDto> getAll() {
        return brandRepository.findAll().stream()
                .map(productMapper::toBrandDto)
                .collect(Collectors.toList());
    }

    public List<BrandDto> search(String name) {
        if (name == null || name.isBlank()) return getAll();
        return brandRepository.findByNameContainingIgnoreCase(name.trim()).stream()
                .map(productMapper::toBrandDto)
                .collect(Collectors.toList());
    }

    public BrandDto getById(UUID id) {
        BrandEntity e = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada: " + id));
        return productMapper.toBrandDto(e);
    }

    @Transactional
    public BrandDto findOrCreateByName(String name) {
        if (name == null || name.isBlank()) return null;
        final String trimmed = name.trim();
        return brandRepository.findByNameIgnoreCase(trimmed)
                .map(productMapper::toBrandDto)
                .orElseGet(() -> {
                    try {
                        return create(BrandDto.builder().name(trimmed).build());
                    } catch (DataIntegrityViolationException ex) {
                        // Si el índice único ya existe y hubo carrera, devolvemos el existente.
                        return brandRepository.findByNameIgnoreCase(trimmed)
                                .map(productMapper::toBrandDto)
                                .orElseThrow(() -> ex);
                    }
                });
    }

    @Transactional
    public BrandDto create(BrandDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre de marca es obligatorio");
        }
        final String trimmed = dto.getName().trim();
        return brandRepository.findByNameIgnoreCase(trimmed)
                .map(productMapper::toBrandDto)
                .orElseGet(() -> {
                    BrandEntity e = BrandEntity.builder().name(trimmed).build();
                    e = brandRepository.save(e);
                    return productMapper.toBrandDto(e);
                });
    }

    @Transactional
    public BrandDto update(UUID id, BrandDto dto) {
        BrandEntity e = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada: " + id));
        if (dto.getName() != null && !dto.getName().isBlank()) {
            final String trimmed = dto.getName().trim();
            brandRepository.findByNameIgnoreCase(trimmed).ifPresent(other -> {
                if (!other.getId().equals(id)) {
                    throw new IllegalArgumentException("Ya existe una marca con ese nombre.");
                }
            });
            e.setName(trimmed);
        }
        e = brandRepository.save(e);
        return productMapper.toBrandDto(e);
    }

    @Transactional
    public void delete(UUID id) {
        if (!brandRepository.existsById(id)) {
            throw new IllegalArgumentException("Marca no encontrada: " + id);
        }
        brandRepository.deleteById(id);
    }
}
