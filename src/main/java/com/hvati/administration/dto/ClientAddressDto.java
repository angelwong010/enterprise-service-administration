package com.hvati.administration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientAddressDto {
    private UUID id;
    private String addressType; // MAIN, SHIPPING, BILLING
    private String street;
    private String exteriorNumber;
    private String interiorNumber;
    private String postalCode;
    private String colonia;
    private String municipio;
    private String city;
    private String state;
    private String country;
}
