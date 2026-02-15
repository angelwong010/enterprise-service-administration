package com.hvati.administration.repository;

import com.hvati.administration.entity.StockMovementEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovementEntity, UUID> {

    List<StockMovementEntity> findByProductIdOrderByCreatedAtDesc(UUID productId, Pageable pageable);
}
