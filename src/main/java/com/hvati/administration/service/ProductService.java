package com.hvati.administration.service;

import com.hvati.administration.dto.*;
import com.hvati.administration.entity.*;
import com.hvati.administration.mapper.ProductMapper;
import com.hvati.administration.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final LocationRepository locationRepository;
    private final PriceListRepository priceListRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductDto> getAll(boolean withPricesAndStock) {
        return productRepository.findAllWithCategoryAndBrand().stream()
                .map(p -> productMapper.toProductDto(p, withPricesAndStock, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> search(String q, boolean withPricesAndStock) {
        if (q == null || q.isBlank()) return getAll(withPricesAndStock);
        String query = q.trim();
        return productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCaseOrBarcodeContainingIgnoreCase(query, query, query)
                .stream()
                .map(p -> productMapper.toProductDto(p, withPricesAndStock, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto getById(UUID id) {
        ProductEntity p = productRepository.findByIdWithCategoryAndBrand(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        ProductDto dto = productMapper.toProductDto(p, true, true);
        List<StockMovementEntity> movements = stockMovementRepository.findByProductIdOrderByCreatedAtDesc(id, PageRequest.of(0, 20));
        productMapper.setLastMovements(dto, movements.stream().map(productMapper::toStockMovementDto).collect(Collectors.toList()));
        return dto;
    }

    @Transactional
    public ProductDto create(ProductDto dto) {
        ProductEntity p = new ProductEntity();
        mapDtoToProduct(dto, p);
        p.setId(null);
        p.setPrices(new ArrayList<>());
        p.setLocationStocks(new ArrayList<>());
        p.setVariants(new ArrayList<>());
        final ProductEntity product = productRepository.save(p);

        if (dto.getPrices() != null) {
            for (ProductPriceItemDto pr : dto.getPrices()) {
                if (pr.getPriceListId() != null && pr.getPrice() != null) {
                    priceListRepository.findById(pr.getPriceListId()).ifPresent(priceList -> {
                        ProductPriceEntity pe = ProductPriceEntity.builder()
                                .product(product)
                                .priceList(priceList)
                                .price(pr.getPrice())
                                .currency(pr.getCurrency() != null ? pr.getCurrency() : "MXN")
                                .build();
                        product.getPrices().add(pe);
                    });
                }
            }
        }
        if (dto.getLocationStocks() != null) {
            for (ProductLocationStockDto ls : dto.getLocationStocks()) {
                if (ls.getLocationId() != null) {
                    locationRepository.findById(ls.getLocationId()).ifPresent(loc -> {
                        ProductLocationStockEntity se = ProductLocationStockEntity.builder()
                                .product(product)
                                .location(loc)
                                .quantity(ls.getQuantity() != null ? ls.getQuantity() : 0)
                                .minQuantity(ls.getMinQuantity() != null ? ls.getMinQuantity() : 0)
                                .build();
                        product.getLocationStocks().add(se);
                    });
                }
            }
        }
        if (dto.getVariants() != null) {
            for (ProductVariantDto vdto : dto.getVariants()) {
                ProductVariantEntity ve = ProductVariantEntity.builder()
                        .product(product)
                        .sku(vdto.getSku())
                        .quantity(vdto.getQuantity() != null ? vdto.getQuantity() : 0)
                        .options(new ArrayList<>())
                        .build();
                if (vdto.getOptions() != null) {
                    for (ProductVariantOptionDto o : vdto.getOptions()) {
                        ve.getOptions().add(ProductVariantOptionEntity.builder()
                                .variant(ve)
                                .optionName(o.getOptionName() != null ? o.getOptionName() : "")
                                .optionValue(o.getOptionValue() != null ? o.getOptionValue() : "")
                                .build());
                    }
                }
                product.getVariants().add(ve);
            }
        }
        productRepository.save(product);
        return getById(product.getId());
    }

    @Transactional
    public ProductDto update(UUID id, ProductDto dto) {
        final ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        mapDtoToProduct(dto, product);

        product.getPrices().clear();
        if (dto.getPrices() != null) {
            for (ProductPriceItemDto pr : dto.getPrices()) {
                if (pr.getPriceListId() != null && pr.getPrice() != null) {
                    priceListRepository.findById(pr.getPriceListId()).ifPresent(priceList -> {
                        product.getPrices().add(ProductPriceEntity.builder()
                                .product(product)
                                .priceList(priceList)
                                .price(pr.getPrice())
                                .currency(pr.getCurrency() != null ? pr.getCurrency() : "MXN")
                                .build());
                    });
                }
            }
        }

        product.getLocationStocks().clear();
        if (dto.getLocationStocks() != null) {
            for (ProductLocationStockDto ls : dto.getLocationStocks()) {
                if (ls.getLocationId() != null) {
                    locationRepository.findById(ls.getLocationId()).ifPresent(loc -> {
                        product.getLocationStocks().add(ProductLocationStockEntity.builder()
                                .product(product)
                                .location(loc)
                                .quantity(ls.getQuantity() != null ? ls.getQuantity() : 0)
                                .minQuantity(ls.getMinQuantity() != null ? ls.getMinQuantity() : 0)
                                .build());
                    });
                }
            }
        }

        product.getVariants().clear();
        if (dto.getVariants() != null) {
            for (ProductVariantDto vdto : dto.getVariants()) {
                ProductVariantEntity ve = ProductVariantEntity.builder()
                        .product(product)
                        .sku(vdto.getSku())
                        .quantity(vdto.getQuantity() != null ? vdto.getQuantity() : 0)
                        .options(new ArrayList<>())
                        .build();
                if (vdto.getOptions() != null) {
                    for (ProductVariantOptionDto o : vdto.getOptions()) {
                        ve.getOptions().add(ProductVariantOptionEntity.builder()
                                .variant(ve)
                                .optionName(o.getOptionName() != null ? o.getOptionName() : "")
                                .optionValue(o.getOptionValue() != null ? o.getOptionValue() : "")
                                .build());
                    }
                }
                product.getVariants().add(ve);
            }
        }

        productRepository.save(product);
        return getById(id);
    }

    @Transactional
    public void delete(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado: " + id);
        }
        productRepository.deleteById(id);
    }

    public List<StockMovementDto> getLastMovements(UUID productId, int limit) {
        return stockMovementRepository.findByProductIdOrderByCreatedAtDesc(productId, PageRequest.of(0, limit))
                .stream()
                .map(productMapper::toStockMovementDto)
                .collect(Collectors.toList());
    }

    private void mapDtoToProduct(ProductDto dto, ProductEntity p) {
        p.setName(dto.getName() != null ? dto.getName() : "");
        p.setSku(dto.getSku());
        p.setBarcode(dto.getBarcode());
        p.setProductType(dto.getProductType() != null ? dto.getProductType() : "PRODUCT");
        if (dto.getCategoryId() != null) {
            categoryRepository.findById(dto.getCategoryId()).ifPresent(p::setCategory);
        } else if (dto.getCategoryName() != null && !dto.getCategoryName().isBlank()) {
            CategoryDto cat = categoryService.findOrCreateByName(dto.getCategoryName());
            if (cat != null) categoryRepository.findById(cat.getId()).ifPresent(p::setCategory);
        }
        if (dto.getBrandId() != null) {
            brandRepository.findById(dto.getBrandId()).ifPresent(p::setBrand);
        } else if (dto.getBrandName() != null && !dto.getBrandName().isBlank()) {
            BrandDto brand = brandService.findOrCreateByName(dto.getBrandName());
            if (brand != null) brandRepository.findById(brand.getId()).ifPresent(p::setBrand);
        } else {
            p.setBrand(null);
        }
        p.setUnitOfSale(dto.getUnitOfSale() != null ? dto.getUnitOfSale() : "Unidad");
        p.setLocationText(dto.getLocationText());
        p.setDescription(dto.getDescription());
        p.setUseStock(dto.getUseStock() != null ? dto.getUseStock() : true);
        p.setUseLotsExpiry(Boolean.TRUE.equals(dto.getUseLotsExpiry()));
        p.setChargeTax(dto.getChargeTax() != null ? dto.getChargeTax() : true);
        p.setIvaRate(dto.getIvaRate() != null ? dto.getIvaRate() : new BigDecimal("16"));
        p.setIepsRate(dto.getIepsRate() != null ? dto.getIepsRate() : BigDecimal.ZERO);
        p.setCostNet(dto.getCostNet());
        p.setCostWithTax(dto.getCostWithTax());
        p.setIncludeInCatalog(dto.getIncludeInCatalog() != null ? dto.getIncludeInCatalog() : true);
        p.setSellAtPos(dto.getSellAtPos() != null ? dto.getSellAtPos() : true);
        p.setRequirePrescription(Boolean.TRUE.equals(dto.getRequirePrescription()));
        p.setAllowManufacturing(Boolean.TRUE.equals(dto.getAllowManufacturing()));
        p.setSatKey(dto.getSatKey());
    }
}
