package com.hvati.administration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientBillingDataDto {
    private UUID id;
    private String razonSocial;
    private String postalCode;
    private String rfc;
    private String regimenFiscal;
    private String street;
    private String exteriorNumber;
    private String interiorNumber;
    private String colonia;
    private String municipio;
    private String city;
    private String state;
}
