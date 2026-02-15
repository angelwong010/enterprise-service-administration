package com.hvati.administration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientDto {
    private UUID id;

    @NotBlank(message = "El campo nombre es requerido y no fue enviado. Debe completarse.")
    @Size(min = 1)
    private String name;

    @NotBlank(message = "El campo apellido es requerido y no fue enviado. Debe completarse.")
    @Size(min = 1)
    private String lastName;

    private String phone;

    @Email(message = "El email no tiene un formato válido.")
    private String email;
    private String comments;
    private UUID priceListId;
    private String priceListName;
    private BigDecimal creditLimit;
    private String clientType;

    private List<ClientAddressDto> addresses;
    private ClientBillingDataDto billingData;

    @AssertTrue(message = "Debe proporcionar al menos uno: email o teléfono. Complete uno de los dos campos.")
    public boolean isEmailOrPhonePresent() {
        boolean hasEmail = email != null && !email.isBlank();
        boolean hasPhone = phone != null && !phone.isBlank();
        return hasEmail || hasPhone;
    }

    // Resumen estado de cuenta (opcional, poblado en GET por id o listado)
    private Integer salesCount;
    private BigDecimal totalSold;
    private BigDecimal pendingDebt;
    private List<SaleSummaryDto> lastSales;
}
