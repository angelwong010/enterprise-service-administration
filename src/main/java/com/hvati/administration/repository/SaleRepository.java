package com.hvati.administration.repository;

import com.hvati.administration.entity.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<SaleEntity, UUID> {

    List<SaleEntity> findByClientIdOrderBySaleDateDesc(UUID clientId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT COALESCE(SUM(s.total), 0) FROM SaleEntity s WHERE s.client.id = :clientId AND s.paymentStatus = 'PAID'")
    BigDecimal sumTotalPaidByClientId(UUID clientId);

    @Query("SELECT COALESCE(SUM(s.total), 0) FROM SaleEntity s WHERE s.client.id = :clientId AND s.paymentStatus = 'PENDING'")
    BigDecimal sumTotalPendingByClientId(UUID clientId);

    Optional<SaleEntity> findTopByOrderBySaleNumberDesc();

    long countByClientId(UUID clientId);
}
