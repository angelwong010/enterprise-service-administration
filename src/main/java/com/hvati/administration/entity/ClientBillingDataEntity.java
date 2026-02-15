package com.hvati.administration.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "client_billing_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientBillingDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    private ClientEntity client;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "postal_code")
    private String postalCode;

    private String rfc;

    @Column(name = "regimen_fiscal")
    private String regimenFiscal;

    private String street;

    @Column(name = "exterior_number")
    private String exteriorNumber;

    @Column(name = "interior_number")
    private String interiorNumber;

    private String colonia;

    private String municipio;

    private String city;

    private String state;
}
