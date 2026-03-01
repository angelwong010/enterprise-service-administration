package com.hvati.administration.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "client_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientAddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @Column(name = "address_type", nullable = false)
    private String addressType;

    private String street;

    @Column(name = "exterior_number")
    private String exteriorNumber;

    @Column(name = "interior_number")
    private String interiorNumber;

    @Column(name = "postal_code")
    private String postalCode;

    private String colonia;

    private String municipio;

    private String city;

    private String state;

    private String country;
}
