package com.hvati.administration.mapper;

import com.hvati.administration.dto.*;
import com.hvati.administration.entity.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientMapper {

    public PriceListDto toPriceListDto(PriceListEntity e) {
        if (e == null) return null;
        return PriceListDto.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .build();
    }

    public ClientAddressDto toAddressDto(ClientAddressEntity e) {
        if (e == null) return null;
        return ClientAddressDto.builder()
                .id(e.getId())
                .addressType(e.getAddressType())
                .street(e.getStreet())
                .exteriorNumber(e.getExteriorNumber())
                .interiorNumber(e.getInteriorNumber())
                .postalCode(e.getPostalCode())
                .colonia(e.getColonia())
                .municipio(e.getMunicipio())
                .city(e.getCity())
                .state(e.getState())
                .country(e.getCountry())
                .build();
    }

    public ClientBillingDataDto toBillingDataDto(ClientBillingDataEntity e) {
        if (e == null) return null;
        return ClientBillingDataDto.builder()
                .id(e.getId())
                .razonSocial(e.getRazonSocial())
                .postalCode(e.getPostalCode())
                .rfc(e.getRfc())
                .regimenFiscal(e.getRegimenFiscal())
                .street(e.getStreet())
                .exteriorNumber(e.getExteriorNumber())
                .interiorNumber(e.getInteriorNumber())
                .colonia(e.getColonia())
                .municipio(e.getMunicipio())
                .city(e.getCity())
                .state(e.getState())
                .build();
    }

    public SaleSummaryDto toSaleSummaryDto(SaleEntity e) {
        if (e == null) return null;
        return SaleSummaryDto.builder()
                .id(e.getId())
                .saleNumber(e.getSaleNumber())
                .total(e.getTotal())
                .currency(e.getCurrency())
                .paymentStatus(e.getPaymentStatus())
                .deliveryStatus(e.getDeliveryStatus())
                .saleDate(e.getSaleDate())
                .build();
    }

    public ClientDto toClientDto(ClientEntity e) {
        return toClientDto(e, null, null, null, null);
    }

    public ClientDto toClientDto(ClientEntity e, Integer salesCount, java.math.BigDecimal totalSold,
                                  java.math.BigDecimal pendingDebt, List<SaleSummaryDto> lastSales) {
        if (e == null) return null;
        List<ClientAddressDto> addresses = e.getAddresses() == null
                ? Collections.emptyList()
                : e.getAddresses().stream().map(this::toAddressDto).collect(Collectors.toList());
        return ClientDto.builder()
                .id(e.getId())
                .name(e.getName())
                .lastName(e.getLastName())
                .phone(e.getPhone())
                .email(e.getEmail())
                .comments(e.getComments())
                .priceListId(e.getPriceList() != null ? e.getPriceList().getId() : null)
                .priceListName(e.getPriceList() != null ? e.getPriceList().getName() : null)
                .creditLimit(e.getCreditLimit())
                .clientType(e.getClientType())
                .addresses(addresses)
                .billingData(toBillingDataDto(e.getBillingData()))
                .salesCount(salesCount)
                .totalSold(totalSold)
                .pendingDebt(pendingDebt)
                .lastSales(lastSales != null ? lastSales : Collections.emptyList())
                .build();
    }
}
