package com.hvati.administration.mapper;

import com.hvati.administration.dto.*;
import com.hvati.administration.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public CategoryDto toCategoryDto(CategoryEntity e) {
        if (e == null) return null;
        return CategoryDto.builder().id(e.getId()).name(e.getName()).build();
    }

    public BrandDto toBrandDto(BrandEntity e) {
        if (e == null) return null;
        return BrandDto.builder().id(e.getId()).name(e.getName()).build();
    }

    public LocationDto toLocationDto(LocationEntity e) {
        if (e == null) return null;
        return LocationDto.builder().id(e.getId()).name(e.getName()).build();
    }

    public ProductVariantOptionDto toVariantOptionDto(ProductVariantOptionEntity e) {
        if (e == null) return null;
        return ProductVariantOptionDto.builder()
                .id(e.getId())
                .optionName(e.getOptionName())
                .optionValue(e.getOptionValue())
                .build();
    }

    public ProductVariantDto toVariantDto(ProductVariantEntity e) {
        if (e == null) return null;
        List<ProductVariantOptionDto> opts = e.getOptions() == null
                ? Collections.emptyList()
                : e.getOptions().stream().map(this::toVariantOptionDto).collect(Collectors.toList());
        return ProductVariantDto.builder()
                .id(e.getId())
                .sku(e.getSku())
                .quantity(e.getQuantity() != null ? e.getQuantity() : 0)
                .options(opts)
                .build();
    }

    public ProductPriceItemDto toPriceItemDto(ProductPriceEntity e, BigDecimal costNet) {
        if (e == null) return null;
        BigDecimal price = e.getPrice();
        BigDecimal marginPercent = null;
        BigDecimal profit = null;
        if (costNet != null && costNet.compareTo(BigDecimal.ZERO) > 0 && price != null && price.compareTo(BigDecimal.ZERO) > 0) {
            profit = price.subtract(costNet);
            marginPercent = profit.multiply(BigDecimal.valueOf(100)).divide(price, 2, RoundingMode.HALF_UP);
        }
        return ProductPriceItemDto.builder()
                .id(e.getId())
                .priceListId(e.getPriceList() != null ? e.getPriceList().getId() : null)
                .priceListName(e.getPriceList() != null ? e.getPriceList().getName() : null)
                .price(price)
                .currency(e.getCurrency())
                .marginPercent(marginPercent)
                .profit(profit)
                .build();
    }

    public ProductLocationStockDto toLocationStockDto(ProductLocationStockEntity e) {
        if (e == null) return null;
        return ProductLocationStockDto.builder()
                .id(e.getId())
                .locationId(e.getLocation() != null ? e.getLocation().getId() : null)
                .locationName(e.getLocation() != null ? e.getLocation().getName() : null)
                .quantity(e.getQuantity() != null ? e.getQuantity() : 0)
                .minQuantity(e.getMinQuantity() != null ? e.getMinQuantity() : 0)
                .build();
    }

    public StockMovementDto toStockMovementDto(StockMovementEntity e) {
        if (e == null) return null;
        return StockMovementDto.builder()
                .id(e.getId())
                .productId(e.getProduct() != null ? e.getProduct().getId() : null)
                .locationId(e.getLocation() != null ? e.getLocation().getId() : null)
                .locationName(e.getLocation() != null ? e.getLocation().getName() : null)
                .movementType(e.getMovementType())
                .quantity(e.getQuantity())
                .previousQuantity(e.getPreviousQuantity())
                .newQuantity(e.getNewQuantity())
                .referenceType(e.getReferenceType())
                .referenceId(e.getReferenceId())
                .userId(e.getUserId())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public ProductDto toProductDto(ProductEntity e) {
        return toProductDto(e, false, false);
    }

    public ProductDto toProductDto(ProductEntity e, boolean withPricesAndStock, boolean withLastMovements) {
        if (e == null) return null;
        List<ProductPriceItemDto> prices = Collections.emptyList();
        List<ProductLocationStockDto> locationStocks = Collections.emptyList();
        List<ProductVariantDto> variants = Collections.emptyList();
        List<StockMovementDto> lastMovements = Collections.emptyList();
        Integer totalStock = 0;
        int variantCount = 0;
        BigDecimal displayPrice = null;

        if (e.getLocationStocks() != null) {
            locationStocks = e.getLocationStocks().stream().map(this::toLocationStockDto).collect(Collectors.toList());
            totalStock = e.getLocationStocks().stream().mapToInt(s -> s.getQuantity() != null ? s.getQuantity() : 0).sum();
        }
        if (e.getVariants() != null && !e.getVariants().isEmpty()) {
            variants = e.getVariants().stream().map(this::toVariantDto).collect(Collectors.toList());
            variantCount = e.getVariants().size();
            int variantStock = e.getVariants().stream().mapToInt(v -> v.getQuantity() != null ? v.getQuantity() : 0).sum();
            if (variantStock > 0) totalStock = variantStock;
        }
        if (withPricesAndStock && e.getPrices() != null && !e.getPrices().isEmpty()) {
            prices = e.getPrices().stream()
                    .map(pp -> toPriceItemDto(pp, e.getCostNet()))
                    .collect(Collectors.toList());
            displayPrice = e.getPrices().stream().findFirst().map(ProductPriceEntity::getPrice).orElse(null);
        }

        return ProductDto.builder()
                .id(e.getId())
                .name(e.getName())
                .sku(e.getSku())
                .barcode(e.getBarcode())
                .productType(e.getProductType())
                .categoryId(e.getCategory() != null ? e.getCategory().getId() : null)
                .categoryName(e.getCategory() != null ? e.getCategory().getName() : null)
                .brandId(e.getBrand() != null ? e.getBrand().getId() : null)
                .brandName(e.getBrand() != null ? e.getBrand().getName() : null)
                .unitOfSale(e.getUnitOfSale())
                .locationText(e.getLocationText())
                .description(e.getDescription())
                .useStock(e.getUseStock())
                .useLotsExpiry(e.getUseLotsExpiry())
                .chargeTax(e.getChargeTax())
                .ivaRate(e.getIvaRate())
                .iepsRate(e.getIepsRate())
                .costNet(e.getCostNet())
                .costWithTax(e.getCostWithTax())
                .includeInCatalog(e.getIncludeInCatalog())
                .sellAtPos(e.getSellAtPos())
                .requirePrescription(e.getRequirePrescription())
                .allowManufacturing(e.getAllowManufacturing())
                .satKey(e.getSatKey())
                .totalStock(totalStock)
                .variantCount(variantCount)
                .displayPrice(displayPrice)
                .prices(prices)
                .locationStocks(locationStocks)
                .variants(variants)
                .lastMovements(lastMovements)
                .build();
    }

    public void setLastMovements(ProductDto dto, List<StockMovementDto> movements) {
        if (dto != null) dto.setLastMovements(movements != null ? movements : Collections.emptyList());
    }
}
