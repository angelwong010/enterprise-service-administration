package com.hvati.administration.service;

import com.hvati.administration.dto.SaleDto;
import com.hvati.administration.dto.SaleHistoryEntryDto;
import com.hvati.administration.dto.SaleLineDto;
import com.hvati.administration.entity.ClientEntity;
import com.hvati.administration.entity.SaleEntity;
import com.hvati.administration.entity.SaleHistoryEntity;
import com.hvati.administration.entity.SaleLineEntity;
import com.hvati.administration.mapper.SaleAppMapper;
import com.hvati.administration.repository.ClientRepository;
import com.hvati.administration.repository.QuotationRepository;
import com.hvati.administration.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleAppService {

    private final SaleRepository saleRepository;
    private final ClientRepository clientRepository;
    private final QuotationRepository quotationRepository;
    private final SaleAppMapper saleAppMapper;

    public List<SaleDto> getSales() {
        return saleRepository.findAll().stream()
                .map(saleAppMapper::toSaleDto)
                .toList();
    }

    public Optional<SaleDto> getSaleById(UUID id) {
        return saleRepository.findById(id)
                .map(saleAppMapper::toSaleDto);
    }

    public Optional<SaleDto> getSaleByNumber(Integer number) {
        return saleRepository.findBySaleNumber(number)
                .map(saleAppMapper::toSaleDto);
    }

    @Transactional
    public SaleDto createSale(SaleDto dto) {
        ClientEntity client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + dto.getClientId()));

        int nextNumber = saleRepository.findTopByOrderBySaleNumberDesc()
                .map(s -> s.getSaleNumber() + 1)
                .orElse(1);

        String status = dto.getStatus() != null ? dto.getStatus() : "pendiente";
        String paymentStatus = toPaymentStatus(status);
        String deliveryStatus = toDeliveryStatus(status);

        SaleEntity sale = new SaleEntity();
        sale.setSaleNumber(nextNumber);
        sale.setClient(client);
        sale.setTotal(dto.getTotal() != null ? dto.getTotal() : BigDecimal.ZERO);
        sale.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "MXN");
        sale.setPaymentStatus(paymentStatus);
        sale.setDeliveryStatus(deliveryStatus);
        sale.setSaleDate(LocalDateTime.now());
        sale.setSubtotal(dto.getSubtotal());
        sale.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO);
        sale.setPaymentMethod(dto.getPaymentMethod());
        sale.setQuotationId(dto.getQuotationId());
        sale.setQuotationNumber(dto.getQuotationNumber());
        sale.setClientName(dto.getClientName());
        sale.setClientAddress(saleAppMapper.addressDtoToMap(dto.getClientAddress()));
        sale.setShippingAddress(saleAppMapper.addressDtoToMap(dto.getShippingAddress()));
        sale.setChannel(dto.getChannel());
        sale.setRegisterId(dto.getRegisterId());
        sale.setCashierId(dto.getCashierId());
        sale.setSalespersonId(dto.getSalespersonId());
        sale.setInvoiceId(dto.getInvoiceId());
        sale.setInvoiceUrl(dto.getInvoiceUrl());
        sale.setStatus(status);

        mapLines(dto.getLines(), sale);
        mapHistory(dto.getHistory(), sale);

        SaleEntity savedSale = saleRepository.save(sale);

        if (dto.getQuotationId() != null) {
            quotationRepository.findById(dto.getQuotationId()).ifPresent(q -> {
                q.setSale(savedSale);
                q.setStatus("accepted");
                quotationRepository.save(q);
            });
        }

        return saleAppMapper.toSaleDto(saleRepository.findById(savedSale.getId()).orElse(savedSale));
    }

    private void mapLines(List<SaleLineDto> lines, SaleEntity sale) {
        if (lines == null) return;
        for (SaleLineDto ldto : lines) {
            SaleLineEntity line = new SaleLineEntity();
            line.setSale(sale);
            line.setProductId(ldto.getProductId());
            line.setProductName(ldto.getProductName() != null ? ldto.getProductName() : "");
            line.setIsShipping(Boolean.TRUE.equals(ldto.getIsShipping()));
            line.setUnitPrice(ldto.getUnitPrice() != null ? ldto.getUnitPrice() : BigDecimal.ZERO);
            line.setQuantity(ldto.getQuantity() != null ? ldto.getQuantity() : BigDecimal.ONE);
            line.setSubtotal(ldto.getSubtotal() != null ? ldto.getSubtotal() : BigDecimal.ZERO);
            line.setCurrency(ldto.getCurrency());
            line.setPriceListId(ldto.getPriceListId());
            line.setPriceListName(ldto.getPriceListName());
            sale.getLines().add(line);
        }
    }

    private void mapHistory(List<SaleHistoryEntryDto> history, SaleEntity sale) {
        if (history == null) return;
        for (SaleHistoryEntryDto hdto : history) {
            SaleHistoryEntity he = new SaleHistoryEntity();
            he.setSale(sale);
            he.setType(hdto.getType() != null ? hdto.getType() : "sale_registered");
            he.setAmount(hdto.getAmount());
            he.setPaymentMethod(hdto.getPaymentMethod());
            he.setQuotationNumber(hdto.getQuotationNumber());
            he.setComment(hdto.getComment());
            he.setUserId(hdto.getUserId());
            sale.getHistory().add(he);
        }
    }

    private static String toPaymentStatus(String status) {
        if ("cancelled".equals(status)) return "CANCELLED";
        if ("finalizada".equals(status) || "por_enviar".equals(status)) return "PAID";
        return "PENDING";
    }

    private static String toDeliveryStatus(String status) {
        if ("cancelled".equals(status)) return "CANCELLED";
        if ("finalizada".equals(status)) return "DELIVERED";
        return "PENDING";
    }
}
