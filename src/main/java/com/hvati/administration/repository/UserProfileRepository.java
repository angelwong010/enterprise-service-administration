package com.hvati.administration.repository;

import com.hvati.administration.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, UUID> {

    Optional<UserProfileEntity> findByKeycloakId(String keycloakId);

    void deleteByKeycloakId(String keycloakId);

    boolean existsByKeycloakId(String keycloakId);
}
