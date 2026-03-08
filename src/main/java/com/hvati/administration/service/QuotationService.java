package com.hvati.administration.service;

import com.hvati.administration.dto.QuotationDto;
import com.hvati.administration.dto.QuotationLineDto;
import com.hvati.administration.dto.SaleDto;
import com.hvati.administration.entity.*;
import com.hvati.administration.mapper.QuotationMapper;
import com.hvati.administration.repository.ClientRepository;
import com.hvati.administration.repository.QuotationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final ClientRepository clientRepository;
    private final SaleAppService saleAppService;
    private final QuotationMapper quotationMapper;

    public List<QuotationDto> getQuotations() {
        return quotationRepository.findAll().stream()
                .map(quotationMapper::toQuotationDto)
                .toList();
    }

    public Optional<QuotationDto> getQuotationById(UUID id) {
        return quotationRepository.findById(id)
                .map(quotationMapper::toQuotationDto);
    }

    @Transactional
    public QuotationDto createQuotation(QuotationDto dto) {
        int nextNumber = quotationRepository.findTopByOrderByNumberDesc()
                .map(q -> q.getNumber() + 1)
                .orElse(1);

        QuotationEntity q = new QuotationEntity();
        q.setNumber(nextNumber);
        q.setStatus(dto.getStatus() != null ? dto.getStatus() : "draft");
        q.setSubtotal(dto.getSubtotal() != null ? dto.getSubtotal() : BigDecimal.ZERO);
        q.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO);
        q.setTotal(dto.getTotal() != null ? dto.getTotal() : BigDecimal.ZERO);
        q.setClientName(dto.getClientName());
        if (dto.getClientId() != null) {
            clientRepository.findById(dto.getClientId()).ifPresent(q::setClient);
        }

        if (dto.getLines() != null) {
            for (QuotationLineDto ldto : dto.getLines()) {
                QuotationLineEntity line = new QuotationLineEntity();
                line.setQuotation(q);
                line.setProductId(ldto.getProductId());
                line.setProductName(ldto.getProductName() != null ? ldto.getProductName() : "");
                line.setIsShipping(Boolean.TRUE.equals(ldto.getIsShipping()));
                line.setUnitPrice(ldto.getUnitPrice() != null ? ldto.getUnitPrice() : BigDecimal.ZERO);
                line.setQuantity(ldto.getQuantity() != null ? ldto.getQuantity() : BigDecimal.ONE);
                line.setSubtotal(ldto.getSubtotal() != null ? ldto.getSubtotal() : BigDecimal.ZERO);
                line.setPriceListId(ldto.getPriceListId());
                line.setPriceListName(ldto.getPriceListName());
                q.getLines().add(line);
            }
        }

        q = quotationRepository.save(q);
        return quotationMapper.toQuotationDto(quotationRepository.findById(q.getId()).orElse(q));
    }

    @Transactional
    public Optional<QuotationDto> updateQuotation(UUID id, QuotationDto dto) {
        log.info("[updateQuotation] START id={}, linesCount={}, subtotal={}, discount={}, total={}, clientId={}",
                id, dto.getLines() != null ? dto.getLines().size() : 0,
                dto.getSubtotal(), dto.getDiscount(), dto.getTotal(), dto.getClientId());
        try {
            Optional<QuotationEntity> opt = quotationRepository.findById(id);
            if (opt.isEmpty()) {
                log.warn("[updateQuotation] Quotation not found id={}", id);
                return Optional.empty();
            }
            QuotationEntity q = opt.get();
            log.info("[updateQuotation] Quotation found id={}, number={}, hasSale={}, currentLines={}",
                    q.getId(), q.getNumber(), q.getSale() != null, q.getLines() != null ? q.getLines().size() : 0);
            if (q.getSale() != null) {
                throw new IllegalArgumentException("No se puede editar una cotización ya convertida en venta.");
            }
            q.setStatus(dto.getStatus() != null ? dto.getStatus() : q.getStatus());
            q.setSubtotal(toBigDecimal(dto.getSubtotal(), BigDecimal.ZERO));
            q.setDiscount(toBigDecimal(dto.getDiscount(), BigDecimal.ZERO));
            q.setTotal(toBigDecimal(dto.getTotal(), BigDecimal.ZERO));
            q.setClientName(dto.getClientName());
            if (dto.getClientId() != null) {
                clientRepository.findById(dto.getClientId()).ifPresent(q::setClient);
            } else {
                q.setClient(null);
            }
            log.info("[updateQuotation] Clearing lines (current size={})", q.getLines().size());
            q.getLines().clear();
            if (dto.getLines() != null) {
                for (int i = 0; i < dto.getLines().size(); i++) {
                    QuotationLineDto ldto = dto.getLines().get(i);
                    log.debug("[updateQuotation] Line[{}] productName={}, quantity={}, unitPrice={}",
                            i, ldto.getProductName(), ldto.getQuantity(), ldto.getUnitPrice());
                    QuotationLineEntity line = new QuotationLineEntity();
                    line.setQuotation(q);
                    line.setProductId(ldto.getProductId());
                    line.setProductName(ldto.getProductName() != null ? ldto.getProductName() : "");
                    line.setIsShipping(Boolean.TRUE.equals(ldto.getIsShipping()));
                    line.setUnitPrice(toBigDecimal(ldto.getUnitPrice(), BigDecimal.ZERO));
                    line.setQuantity(toBigDecimal(ldto.getQuantity(), BigDecimal.ONE));
                    line.setSubtotal(toBigDecimal(ldto.getSubtotal(), BigDecimal.ZERO));
                    line.setPriceListId(ldto.getPriceListId());
                    line.setPriceListName(ldto.getPriceListName());
                    q.getLines().add(line);
                }
            }
            log.info("[updateQuotation] Saving quotation, new lines count={}", q.getLines().size());
            q = quotationRepository.save(q);
            log.info("[updateQuotation] Saved, loading for response");
            QuotationDto result = quotationMapper.toQuotationDto(quotationRepository.findById(q.getId()).orElse(q));
            log.info("[updateQuotation] SUCCESS id={}", id);
            return Optional.of(result);
        } catch (Exception e) {
            log.error("[updateQuotation] FAILED id={}, error={}", id, e.getMessage(), e);
            throw e;
        }
    }

    private static BigDecimal toBigDecimal(Number n, BigDecimal defaultValue) {
        if (n == null) return defaultValue;
        if (n instanceof BigDecimal bd) return bd;
        try {
            return BigDecimal.valueOf(n.doubleValue());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Convierte una cotización en venta: crea la venta con quotationId y el SaleAppService actualiza la cotización.
     * Alternativa: crear la venta aquí con los datos de la cotización y devolverla.
     */
    @Transactional
    public Optional<SaleDto> convertQuotationToSale(UUID quotationId) {
        QuotationEntity quotation = quotationRepository.findById(quotationId)
                .orElse(null);
        if (quotation == null || quotation.getSale() != null) {
            return Optional.empty();
        }
        SaleDto saleDto = buildSaleFromQuotation(quotation);
        SaleDto created = saleAppService.createSale(saleDto);
        return Optional.of(created);
    }

    private SaleDto buildSaleFromQuotation(QuotationEntity q) {
        return SaleDto.builder()
                .quotationId(q.getId())
                .quotationNumber(q.getNumber())
                .clientId(q.getClient() != null ? q.getClient().getId() : null)
                .clientName(q.getClientName())
                .lines(q.getLines().stream().map(this::toSaleLineDto).toList())
                .subtotal(q.getSubtotal())
                .discount(q.getDiscount())
                .total(q.getTotal())
                .status("pendiente")
                .currency("MXN")
                .build();
    }

    private com.hvati.administration.dto.SaleLineDto toSaleLineDto(QuotationLineEntity e) {
        return com.hvati.administration.dto.SaleLineDto.builder()
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
