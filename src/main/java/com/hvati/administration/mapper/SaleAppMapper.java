package com.hvati.administration.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hvati.administration.dto.SaleAddressDto;
import com.hvati.administration.dto.SaleDto;
import com.hvati.administration.dto.SaleHistoryEntryDto;
import com.hvati.administration.dto.SaleLineDto;
import com.hvati.administration.entity.SaleEntity;
import com.hvati.administration.entity.SaleHistoryEntity;
import com.hvati.administration.entity.SaleLineEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SaleAppMapper {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE_TIME;

    private final ObjectMapper objectMapper;

    public SaleDto toSaleDto(SaleEntity e) {
        if (e == null) return null;
        List<SaleLineDto> lines = toLineDtos(e.getLines());
        List<SaleHistoryEntryDto> history = toHistoryDtos(e.getHistory());
        String status = e.getStatus() != null ? e.getStatus() : statusFromLegacy(e);
        return SaleDto.builder()
                .id(e.getId())
                .saleNumber(e.getSaleNumber())
                .status(status)
                .lines(lines)
                .subtotal(e.getSubtotal())
                .discount(e.getDiscount() != null ? e.getDiscount() : BigDecimal.ZERO)
                .total(e.getTotal())
                .currency(e.getCurrency())
                .history(history)
                .quotationId(e.getQuotationId())
                .quotationNumber(e.getQuotationNumber())
                .createdAt(e.getCreatedAt() != null ? e.getCreatedAt().format(ISO) : null)
                .channel(e.getChannel())
                .registerId(e.getRegisterId())
                .cashierId(e.getCashierId())
                .salespersonId(e.getSalespersonId())
                .clientId(e.getClient() != null ? e.getClient().getId() : null)
                .clientName(e.getClientName() != null ? e.getClientName() : (e.getClient() != null ? e.getClient().getName() : null))
                .clientAddress(addressMapToDto(e.getClientAddress()))
                .shippingAddress(addressMapToDto(e.getShippingAddress()))
                .invoiceId(e.getInvoiceId())
                .invoiceUrl(e.getInvoiceUrl())
                .paymentMethod(e.getPaymentMethod())
                .build();
    }

    private List<SaleLineDto> toLineDtos(List<SaleLineEntity> list) {
        if (list == null) return Collections.emptyList();
        List<SaleLineDto> out = new ArrayList<>();
        for (SaleLineEntity le : list) out.add(toLineDto(le));
        return out;
    }

    private List<SaleHistoryEntryDto> toHistoryDtos(List<SaleHistoryEntity> list) {
        if (list == null) return Collections.emptyList();
        List<SaleHistoryEntryDto> out = new ArrayList<>();
        for (SaleHistoryEntity he : list) out.add(toHistoryDto(he));
        return out;
    }

    public SaleLineDto toLineDto(SaleLineEntity e) {
        if (e == null) return null;
        return SaleLineDto.builder()
                .id(e.getId())
                .productId(e.getProductId())
                .productName(e.getProductName())
                .isShipping(Boolean.TRUE.equals(e.getIsShipping()))
                .unitPrice(e.getUnitPrice())
                .quantity(e.getQuantity())
                .subtotal(e.getSubtotal())
                .currency(e.getCurrency())
                .priceListId(e.getPriceListId())
                .priceListName(e.getPriceListName())
                .build();
    }

    public SaleHistoryEntryDto toHistoryDto(SaleHistoryEntity e) {
        if (e == null) return null;
        String createdAt = e.getCreatedAt() != null ? e.getCreatedAt().format(ISO) : null;
        return SaleHistoryEntryDto.builder()
                .id(e.getId())
                .type(e.getType())
                .userId(e.getUserId())
                .amount(e.getAmount())
                .comment(e.getComment())
                .quotationNumber(e.getQuotationNumber())
                .paymentMethod(e.getPaymentMethod())
                .createdAt(createdAt)
                .build();
    }

    private String statusFromLegacy(SaleEntity e) {
        if ("CANCELLED".equals(e.getPaymentStatus())) return "cancelled";
        if ("PAID".equals(e.getPaymentStatus()) && "DELIVERED".equals(e.getDeliveryStatus())) return "finalizada";
        if ("PAID".equals(e.getPaymentStatus())) return "por_enviar";
        return "pendiente";
    }

    /**
     * Converts address Map from entity (jsonb) to DTO. Used when reading from DB.
     */
    public SaleAddressDto addressMapToDto(Map<String, Object> map) {
        if (map == null || map.isEmpty()) return null;
        try {
            return objectMapper.convertValue(map, SaleAddressDto.class);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Converts address DTO to Map for persisting as jsonb. Used when writing to DB.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> addressDtoToMap(SaleAddressDto dto) {
        if (dto == null) return null;
        try {
            return objectMapper.convertValue(dto, Map.class);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
