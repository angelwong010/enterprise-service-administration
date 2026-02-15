package com.hvati.administration.service;

import com.hvati.administration.dto.LocationDto;
import com.hvati.administration.mapper.ProductMapper;
import com.hvati.administration.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final ProductMapper productMapper;

    public List<LocationDto> getAll() {
        return locationRepository.findAll().stream()
                .map(productMapper::toLocationDto)
                .collect(Collectors.toList());
    }

    public LocationDto getById(UUID id) {
        return productMapper.toLocationDto(
                locationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ubicación no encontrada: " + id)));
    }
}
