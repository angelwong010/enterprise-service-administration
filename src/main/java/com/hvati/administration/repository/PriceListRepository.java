package com.hvati.administration.repository;

import com.hvati.administration.entity.PriceListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PriceListRepository extends JpaRepository<PriceListEntity, UUID> {

    java.util.Optional<PriceListEntity> findByNameIgnoreCase(String name);
}
