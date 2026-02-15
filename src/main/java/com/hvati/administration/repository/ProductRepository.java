package com.hvati.administration.repository;

import com.hvati.administration.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand ORDER BY p.name")
    List<ProductEntity> findAllWithCategoryAndBrand();

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithCategoryAndBrand(UUID id);

    List<ProductEntity> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCaseOrBarcodeContainingIgnoreCase(
            String name, String sku, String barcode);
}
