package com.hvati.administration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrandDto {
    private UUID id;

    @NotBlank(message = "El campo nombre es requerido y no fue enviado. Debe completarse.")
    @Size(min = 1)
    private String name;
}
