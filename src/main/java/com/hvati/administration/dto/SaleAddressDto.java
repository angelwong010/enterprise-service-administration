package com.hvati.administration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaleAddressDto {
    private String street;

    @JsonProperty("exteriorNumber")
    private String exteriorNumber;

    @JsonProperty("interiorNumber")
    private String interiorNumber;

    @JsonProperty("postalCode")
    private String postalCode;

    private String colonia;
    private String municipio;
    private String city;
    private String state;
    private String country;
}
