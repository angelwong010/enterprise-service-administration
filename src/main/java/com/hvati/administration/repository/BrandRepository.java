package com.hvati.administration.repository;

import com.hvati.administration.entity.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<BrandEntity, UUID> {

    List<BrandEntity> findByNameContainingIgnoreCase(String name);

    Optional<BrandEntity> findByNameIgnoreCase(String name);
}
