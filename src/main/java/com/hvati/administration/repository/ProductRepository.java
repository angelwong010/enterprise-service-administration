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

    /** Loads product with category, brand, prices and each price's priceList for list/detail with prices. */
    @Query("SELECT DISTINCT p FROM ProductEntity p " +
            "LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand " +
            "LEFT JOIN FETCH p.prices pr LEFT JOIN FETCH pr.priceList " +
            "ORDER BY p.name")
    List<ProductEntity> findAllWithCategoryBrandAndPrices();

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithCategoryAndBrand(UUID id);

    /** Loads product with category, brand, prices and each price's priceList for detail view. locationStocks loaded lazily. */
    @Query("SELECT p FROM ProductEntity p " +
            "LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand " +
            "LEFT JOIN FETCH p.prices pr LEFT JOIN FETCH pr.priceList " +
            "WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithCategoryBrandPricesAndStocks(UUID id);

    List<ProductEntity> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCaseOrBarcodeContainingIgnoreCase(
            String name, String sku, String barcode);

    /** Search with prices and priceList loaded for list with prices. */
    @Query("SELECT DISTINCT p FROM ProductEntity p " +
            "LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand " +
            "LEFT JOIN FETCH p.prices pr LEFT JOIN FETCH pr.priceList " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "OR LOWER(p.barcode) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "ORDER BY p.name")
    List<ProductEntity> searchWithCategoryBrandAndPrices(String q);
}
