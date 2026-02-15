package com.hvati.administration.repository;

import com.hvati.administration.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    @Query("SELECT c FROM ClientEntity c LEFT JOIN FETCH c.priceList ORDER BY c.name, c.lastName")
    List<ClientEntity> findAllWithPriceList();

    List<ClientEntity> findByNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String lastName, String email);
}
