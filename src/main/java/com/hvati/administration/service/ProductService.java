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
    private final ProductPriceRepository productPriceRepository;
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
        List<ProductEntity> list = withPricesAndStock
                ? productRepository.findAllWithCategoryBrandAndPrices()
                : productRepository.findAllWithCategoryAndBrand();
        return list.stream()
                .map(p -> productMapper.toProductDto(p, withPricesAndStock, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> search(String q, boolean withPricesAndStock) {
        if (q == null || q.isBlank()) return getAll(withPricesAndStock);
        String query = q.trim();
        List<ProductEntity> list = withPricesAndStock
                ? productRepository.searchWithCategoryBrandAndPrices(query)
                : productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCaseOrBarcodeContainingIgnoreCase(query, query, query);
        return list.stream()
                .map(p -> productMapper.toProductDto(p, withPricesAndStock, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto getById(UUID id) {
        ProductEntity p = productRepository.findByIdWithCategoryBrandPricesAndStocks(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        ProductDto dto = productMapper.toProductDto(p, true, true);
        List<StockMovementEntity> movements = stockMovementRepository.findByProductIdOrderByCreatedAtDesc(id, PageRequest.of(0, 20));
        productMapper.setLastMovements(dto, movements.stream().map(productMapper::toStockMovementDto).collect(Collectors.toList()));
        return dto;
    }

    @Transactional
    public ProductDto create(ProductDto dto) {
        log.info("[ProductService.create] product name={}", dto.getName());

        // Idempotencia: si se vuelve a subir el mismo listado (carga masiva),
        // no creamos duplicados. Si existe por SKU o barcode, actualizamos el existente.
        final String skuKey =
                dto.getSku() != null && !dto.getSku().isBlank() ? dto.getSku().trim() : null;
        if (skuKey != null) {
            var existing = productRepository.findFirstBySkuIgnoreCase(skuKey);
            if (existing.isPresent()) {
                log.info("[ProductService.create] UPSERT by SKU. existingId={}, sku={}", existing.get().getId(), skuKey);
                return update(existing.get().getId(), dto);
            }
        }

        final String barcodeKey =
                dto.getBarcode() != null && !dto.getBarcode().isBlank() ? dto.getBarcode().trim() : null;
        if (barcodeKey != null) {
            var existing = productRepository.findFirstByBarcodeIgnoreCase(barcodeKey);
            if (existing.isPresent()) {
                log.info("[ProductService.create] UPSERT by BARCODE. existingId={}, barcode={}", existing.get().getId(), barcodeKey);
                return update(existing.get().getId(), dto);
            }
        }

        // Si no hay SKU/Barcode, intentamos deduplicar por (nombre + marca)
        final String nameKey =
                dto.getName() != null && !dto.getName().isBlank() ? dto.getName().trim() : null;
        if (nameKey != null) {
            UUID brandId = null;
            if (dto.getBrandId() != null) {
                brandId = dto.getBrandId();
            } else if (dto.getBrandName() != null && !dto.getBrandName().isBlank()) {
                // Asegura que exista la marca y nos devuelve su ID para match consistente
                var brand = brandService.findOrCreateByName(dto.getBrandName());
                brandId = brand != null ? brand.getId() : null;
            }

            var existing = productRepository.findFirstForUpsertByNameAndBrand(nameKey, brandId);
            if (existing.isPresent()) {
                log.info("[ProductService.create] UPSERT by (NAME+BRAND). existingId={}, name={}, brandId={}",
                        existing.get().getId(), nameKey, brandId);
                return update(existing.get().getId(), dto);
            }
        }

        // Log incoming DTO for carga masiva
        if (dto.getPrices() == null) {
            log.info("[ProductService.create] product_prices: dto.prices is NULL");
        } else {
            log.info("[ProductService.create] product_prices: dto.prices size={}", dto.getPrices().size());
            for (int i = 0; i < dto.getPrices().size(); i++) {
                ProductPriceItemDto pr = dto.getPrices().get(i);
                log.info("[ProductService.create]   price[{}] priceListId={}, price={}, currency={}",
                        i, pr.getPriceListId(), pr.getPrice(), pr.getCurrency());
            }
        }
        if (dto.getLocationStocks() == null) {
            log.info("[ProductService.create] product_location_stock: dto.locationStocks is NULL");
        } else {
            log.info("[ProductService.create] product_location_stock: dto.locationStocks size={}", dto.getLocationStocks().size());
            for (int i = 0; i < dto.getLocationStocks().size(); i++) {
                ProductLocationStockDto ls = dto.getLocationStocks().get(i);
                log.info("[ProductService.create]   locationStock[{}] locationId={}, quantity={}, minQuantity={}",
                        i, ls.getLocationId(), ls.getQuantity(), ls.getMinQuantity());
            }
        }
        if (dto.getVariants() == null) {
            log.info("[ProductService.create] product_variants: dto.variants is NULL");
        } else {
            log.info("[ProductService.create] product_variants: dto.variants size={}", dto.getVariants().size());
            for (int i = 0; i < dto.getVariants().size(); i++) {
                ProductVariantDto v = dto.getVariants().get(i);
                log.info("[ProductService.create]   variant[{}] sku={}, quantity={}, options={}",
                        i, v.getSku(), v.getQuantity(), v.getOptions() != null ? v.getOptions().size() : 0);
            }
        }

        ProductEntity p = new ProductEntity();
        mapDtoToProduct(dto, p);
        p.setId(null);
        p.setPrices(new ArrayList<>());
        p.setLocationStocks(new ArrayList<>());
        p.setVariants(new ArrayList<>());
        final ProductEntity product = productRepository.save(p);
        log.info("[ProductService.create] product SAVED id={}", product.getId());

        if (dto.getPrices() != null) {
            for (ProductPriceItemDto pr : dto.getPrices()) {
                if (pr.getPriceListId() != null && pr.getPrice() != null) {
                    var optPriceList = priceListRepository.findById(pr.getPriceListId());
                    if (optPriceList.isEmpty()) {
                        log.warn("[ProductService.create] product_prices: priceListId NOT FOUND in DB: {}", pr.getPriceListId());
                        continue;
                    }
                    var priceList = optPriceList.get();
                    ProductPriceEntity pe = ProductPriceEntity.builder()
                            .product(product)
                            .priceList(priceList)
                            .price(pr.getPrice())
                            .currency(pr.getCurrency() != null ? pr.getCurrency() : "MXN")
                            .build();
                    product.getPrices().add(pe);
                    productPriceRepository.saveAndFlush(pe);
                    log.info("[ProductService.create] product_prices: SAVED productId={}, priceListId={}, priceListName={}, price={}",
                            product.getId(), priceList.getId(), priceList.getName(), pr.getPrice());
                } else {
                    log.warn("[ProductService.create] product_prices: SKIP entry (priceListId or price null) priceListId={}, price={}",
                            pr.getPriceListId(), pr.getPrice());
                }
            }
        }

        if (dto.getLocationStocks() != null) {
            for (ProductLocationStockDto ls : dto.getLocationStocks()) {
                if (ls.getLocationId() != null) {
                    var optLoc = locationRepository.findById(ls.getLocationId());
                    if (optLoc.isEmpty()) {
                        log.warn("[ProductService.create] product_location_stock: locationId NOT FOUND: {}", ls.getLocationId());
                        continue;
                    }
                    var loc = optLoc.get();
                    ProductLocationStockEntity se = ProductLocationStockEntity.builder()
                            .product(product)
                            .location(loc)
                            .quantity(ls.getQuantity() != null ? ls.getQuantity() : 0)
                            .minQuantity(ls.getMinQuantity() != null ? ls.getMinQuantity() : 0)
                            .build();
                    product.getLocationStocks().add(se);
                    log.info("[ProductService.create] product_location_stock: ADDED (cascade) productId={}, locationId={}, quantity={}",
                            product.getId(), loc.getId(), se.getQuantity());
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
                log.info("[ProductService.create] product_variants: ADDED (cascade) productId={}, sku={}, optionsCount={}",
                        product.getId(), ve.getSku(), ve.getOptions() != null ? ve.getOptions().size() : 0);
            }
        }

        productRepository.saveAndFlush(product);
        log.info("[ProductService.create] product flush done, returning getById");
        return getById(product.getId());
    }

    @Transactional
    public ProductDto update(UUID id, ProductDto dto) {
        final ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        mapDtoToProduct(dto, product);

        // Eliminar precios existentes en BD antes de insertar los nuevos, para no violar
        // la restricción única (product_id, price_list_id).
        productPriceRepository.deleteByProductId(product.getId());
        product.getPrices().clear();

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
                        productPriceRepository.saveAndFlush(pe);
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
