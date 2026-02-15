package com.hvati.administration.repository;

import com.hvati.administration.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    Optional<CategoryEntity> findByNameIgnoreCase(String name);

    List<CategoryEntity> findByNameContainingIgnoreCase(String name);
}
