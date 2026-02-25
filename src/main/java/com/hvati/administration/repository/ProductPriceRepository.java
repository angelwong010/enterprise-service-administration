package com.hvati.administration.repository;

import com.hvati.administration.entity.ProductPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPriceEntity, UUID> {
}
