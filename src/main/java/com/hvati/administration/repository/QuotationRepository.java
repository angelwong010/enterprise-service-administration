package com.hvati.administration.repository;

import com.hvati.administration.entity.QuotationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuotationRepository extends JpaRepository<QuotationEntity, UUID> {

    Optional<QuotationEntity> findTopByOrderByNumberDesc();

    long countByStatus(String status);
}
