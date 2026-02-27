package com.hvati.administration.mapper;

import com.hvati.administration.dto.QuotationDto;
import com.hvati.administration.dto.QuotationLineDto;
import com.hvati.administration.entity.QuotationEntity;
import com.hvati.administration.entity.QuotationLineEntity;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuotationMapper {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE_TIME;

    public QuotationDto toQuotationDto(QuotationEntity e) {
        if (e == null) return null;
        List<QuotationLineDto> lines = e.getLines() == null
                ? Collections.emptyList()
                : e.getLines().stream().map(this::toLineDto).collect(Collectors.toList());
        return QuotationDto.builder()
                .id(e.getId())
                .number(e.getNumber())
                .clientId(e.getClient() != null ? e.getClient().getId() : null)
                .clientName(e.getClientName())
                .lines(lines)
                .subtotal(e.getSubtotal())
                .discount(e.getDiscount())
                .total(e.getTotal())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt() != null ? e.getCreatedAt().format(ISO) : null)
                .updatedAt(e.getUpdatedAt() != null ? e.getUpdatedAt().format(ISO) : null)
                .saleId(e.getSale() != null ? e.getSale().getId() : null)
                .build();
    }

    public QuotationLineDto toLineDto(QuotationLineEntity e) {
        if (e == null) return null;
        return QuotationLineDto.builder()
                .id(e.getId())
                .productId(e.getProductId())
                .productName(e.getProductName())
                .isShipping(Boolean.TRUE.equals(e.getIsShipping()))
                .unitPrice(e.getUnitPrice())
                .quantity(e.getQuantity())
                .subtotal(e.getSubtotal())
                .priceListId(e.getPriceListId())
                .priceListName(e.getPriceListName())
                .build();
    }
}
