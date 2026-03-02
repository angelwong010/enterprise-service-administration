package com.hvati.administration.service;

import com.hvati.administration.dto.*;
import com.hvati.administration.entity.*;
import com.hvati.administration.mapper.ClientMapper;
import com.hvati.administration.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final PriceListRepository priceListRepository;
    private final SaleRepository saleRepository;
    private final ClientMapper clientMapper;

    public List<ClientDto> getAllClients(boolean withSummary) {
        List<ClientEntity> list = clientRepository.findAllWithPriceList();
        return list.stream()
                .map(c -> toDtoWithOptionalSummary(c, withSummary))
                .collect(Collectors.toList());
    }

    public List<ClientDto> searchClients(String query) {
        if (query == null || query.isBlank()) {
            return getAllClients(false);
        }
        String q = query.trim();
        List<ClientEntity> list = clientRepository
                .findByNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, q);
        return list.stream()
                .map(c -> clientMapper.toClientDto(c))
                .collect(Collectors.toList());
    }

    public ClientDto getClientById(UUID id) {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
        return toDtoWithOptionalSummary(client, true);
    }

    @Transactional
    public ClientDto createClient(ClientDto dto) {
        ClientEntity client = new ClientEntity();
        mapDtoToEntity(dto, client);
        client.setId(null);

        if (dto.getAddresses() != null && !dto.getAddresses().isEmpty()) {
            for (ClientAddressDto a : dto.getAddresses()) {
                client.getAddresses().add(mapAddressDtoToEntity(a, client));
            }
        }
        if (dto.getBillingData() != null) {
            client.setBillingData(mapBillingDtoToEntity(dto.getBillingData(), client));
        }
        client = clientRepository.save(client);
        return clientMapper.toClientDto(clientRepository.findById(client.getId()).orElse(client));
    }

    @Transactional
    public ClientDto updateClient(UUID id, ClientDto dto) {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
        mapDtoToEntity(dto, client);

        client.getAddresses().clear();
        if (dto.getAddresses() != null) {
            for (ClientAddressDto a : dto.getAddresses()) {
                ClientAddressEntity addr = mapAddressDtoToEntity(a, client);
                client.getAddresses().add(addr);
            }
        }

        if (dto.getBillingData() != null) {
            ClientBillingDataEntity billing = client.getBillingData();
            if (billing == null) {
                billing = mapBillingDtoToEntity(dto.getBillingData(), client);
                client.setBillingData(billing);
            } else {
                mapBillingDtoToEntity(dto.getBillingData(), billing);
            }
        } else {
            client.setBillingData(null);
        }

        client = clientRepository.save(client);
        return getClientById(client.getId());
    }

    @Transactional
    public void deleteClient(UUID id) {
        if (!clientRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente no encontrado: " + id);
        }
        clientRepository.deleteById(id);
    }

    public List<SaleSummaryDto> getClientSales(UUID clientId, int limit) {
        return saleRepository.findByClientIdOrderBySaleDateDesc(clientId, PageRequest.of(0, limit))
                .stream()
                .map(clientMapper::toSaleSummaryDto)
                .collect(Collectors.toList());
    }

    public List<PriceListDto> getAllPriceLists() {
        return priceListRepository.findAll().stream()
                .map(clientMapper::toPriceListDto)
                .collect(Collectors.toList());
    }

    /**
     * Find a price list by name, or create it if it does not exist.
     */
    @Transactional
    public PriceListDto findOrCreatePriceListByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        final String trimmed = name.trim();
        return priceListRepository.findByNameIgnoreCase(trimmed)
                .map(clientMapper::toPriceListDto)
                .orElseGet(() -> {
                    try {
                        PriceListEntity e = PriceListEntity.builder().name(trimmed).build();
                        e = priceListRepository.save(e);
                        return clientMapper.toPriceListDto(e);
                    } catch (DataIntegrityViolationException ex) {
                        return priceListRepository.findByNameIgnoreCase(trimmed)
                                .map(clientMapper::toPriceListDto)
                                .orElseThrow(() -> ex);
                    }
                });
    }

    private ClientDto toDtoWithOptionalSummary(ClientEntity c, boolean withSummary) {
        if (!withSummary) {
            return clientMapper.toClientDto(c);
        }
        BigDecimal totalSold = saleRepository.sumTotalPaidByClientId(c.getId());
        BigDecimal pendingDebt = saleRepository.sumTotalPendingByClientId(c.getId());
        List<SaleEntity> lastSales = saleRepository.findByClientIdOrderBySaleDateDesc(c.getId(), PageRequest.of(0, 10));
        int count = (int) saleRepository.countByClientId(c.getId());
        List<SaleSummaryDto> lastSalesDto = lastSales.stream().map(clientMapper::toSaleSummaryDto).collect(Collectors.toList());
        return clientMapper.toClientDto(c, count, totalSold, pendingDebt, lastSalesDto);
    }

    private void mapDtoToEntity(ClientDto dto, ClientEntity e) {
        e.setName(dto.getName() != null ? dto.getName() : "");
        e.setLastName(dto.getLastName());
        e.setPhone(dto.getPhone());
        e.setEmail(dto.getEmail());
        e.setComments(dto.getComments());
        e.setCreditLimit(dto.getCreditLimit());
        e.setClientType(dto.getClientType() != null ? dto.getClientType() : "CONSUMER");
        if (dto.getPriceListId() != null) {
            priceListRepository.findById(dto.getPriceListId()).ifPresent(e::setPriceList);
        } else {
            e.setPriceList(null);
        }
    }

    private ClientAddressEntity mapAddressDtoToEntity(ClientAddressDto dto, ClientEntity client) {
        ClientAddressEntity e = new ClientAddressEntity();
        e.setClient(client);
        e.setAddressType(dto.getAddressType() != null ? dto.getAddressType() : "MAIN");
        e.setStreet(dto.getStreet());
        e.setExteriorNumber(dto.getExteriorNumber());
        e.setInteriorNumber(dto.getInteriorNumber());
        e.setPostalCode(dto.getPostalCode());
        e.setColonia(dto.getColonia());
        e.setMunicipio(dto.getMunicipio());
        e.setCity(dto.getCity());
        e.setState(dto.getState());
        e.setCountry(dto.getCountry() != null ? dto.getCountry() : "México");
        return e;
    }

    private ClientBillingDataEntity mapBillingDtoToEntity(ClientBillingDataDto dto, ClientEntity client) {
        ClientBillingDataEntity e = new ClientBillingDataEntity();
        e.setClient(client);
        e.setRazonSocial(dto.getRazonSocial());
        e.setPostalCode(dto.getPostalCode());
        e.setRfc(dto.getRfc());
        e.setRegimenFiscal(dto.getRegimenFiscal());
        e.setStreet(dto.getStreet());
        e.setExteriorNumber(dto.getExteriorNumber());
        e.setInteriorNumber(dto.getInteriorNumber());
        e.setColonia(dto.getColonia());
        e.setMunicipio(dto.getMunicipio());
        e.setCity(dto.getCity());
        e.setState(dto.getState());
        return e;
    }

    private void mapBillingDtoToEntity(ClientBillingDataDto dto, ClientBillingDataEntity e) {
        e.setRazonSocial(dto.getRazonSocial());
        e.setPostalCode(dto.getPostalCode());
        e.setRfc(dto.getRfc());
        e.setRegimenFiscal(dto.getRegimenFiscal());
        e.setStreet(dto.getStreet());
        e.setExteriorNumber(dto.getExteriorNumber());
        e.setInteriorNumber(dto.getInteriorNumber());
        e.setColonia(dto.getColonia());
        e.setMunicipio(dto.getMunicipio());
        e.setCity(dto.getCity());
        e.setState(dto.getState());
    }
}
