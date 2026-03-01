package com.hvati.administration.service;

import com.hvati.administration.dto.CategoryDto;
import com.hvati.administration.entity.CategoryEntity;
import com.hvati.administration.mapper.ProductMapper;
import com.hvati.administration.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public List<CategoryDto> getAll() {
        return categoryRepository.findAll().stream()
                .map(productMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public List<CategoryDto> search(String name) {
        if (name == null || name.isBlank()) return getAll();
        return categoryRepository.findByNameContainingIgnoreCase(name.trim()).stream()
                .map(productMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getById(UUID id) {
        CategoryEntity e = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + id));
        return productMapper.toCategoryDto(e);
    }

    @Transactional
    public CategoryDto findOrCreateByName(String name) {
        if (name == null || name.isBlank()) return null;
        return categoryRepository.findByNameIgnoreCase(name.trim())
                .map(productMapper::toCategoryDto)
                .orElseGet(() -> create(CategoryDto.builder().name(name.trim()).build()));
    }

    @Transactional
    public CategoryDto create(CategoryDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre de categoría es obligatorio");
        }
        CategoryEntity e = CategoryEntity.builder().name(dto.getName().trim()).build();
        e = categoryRepository.save(e);
        return productMapper.toCategoryDto(e);
    }

    @Transactional
    public CategoryDto update(UUID id, CategoryDto dto) {
        CategoryEntity e = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + id));
        if (dto.getName() != null && !dto.getName().isBlank()) {
            e.setName(dto.getName().trim());
        }
        e = categoryRepository.save(e);
        return productMapper.toCategoryDto(e);
    }

    @Transactional
    public void delete(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Categoría no encontrada: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
