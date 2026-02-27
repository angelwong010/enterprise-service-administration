package com.hvati.administration.config;

import com.hvati.administration.service.KeycloakRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Al levantar el software en un servidor nuevo, asegura que los roles de negocio
 * existan en Keycloak. Si no existen, los crea para mantener congruencia con
 * el frontend y con role_permissions en la BD.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakSeedRunner implements ApplicationRunner {

    private static final List<Map.Entry<String, String>> ROLES_TO_ENSURE = List.of(
            Map.entry("ADMIN", "Administrador con acceso completo a usuarios, roles, ventas, clientes, productos, caja y reportes"),
            Map.entry("VENTAS", "Rol de ventas: órdenes, ventas, clientes, productos y caja")
    );

    private final KeycloakRoleService keycloakRoleService;

    @Override
    public void run(ApplicationArguments args) {
        for (Map.Entry<String, String> role : ROLES_TO_ENSURE) {
            ensureRole(role.getKey(), role.getValue());
        }
    }

    private void ensureRole(String name, String description) {
        try {
            keycloakRoleService.getRoleByName(name);
            log.debug("Keycloak role '{}' already exists", name);
        } catch (Exception e) {
            try {
                keycloakRoleService.createRole(name, description);
                log.info("Created Keycloak role '{}' for new server setup", name);
            } catch (Exception ex) {
                log.warn("Could not create Keycloak role '{}': {}. Create it manually in the realm.", name, ex.getMessage());
            }
        }
    }
}
