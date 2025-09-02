package com.example.vaultpostgres.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VaultPropertyServiceImpl implements VaultPropertyService {
    private final VaultTemplate vaultTemplate;
    private final ConfigurableEnvironment environment;
    @Value("${vault.kv.backend}")
    private String kvBackend;
    @Value("${vault.kv.application-name}")
    private String kvApplicationName;

    @Override
    public MapPropertySource reloadProperties() {
        VaultKeyValueOperations kv = vaultTemplate.opsForKeyValue(kvBackend, VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);
        VaultResponse vaultResponse = kv.get(kvApplicationName);
        MapPropertySource vaultProps = null;
        if (vaultResponse != null && vaultResponse.getData() != null) {
            Map<String, Object> data = vaultResponse.getData();
            if (data != null) {
                vaultProps = new MapPropertySource("vault", data);
                environment.getPropertySources().addFirst(vaultProps);
            }
        }
        return vaultProps;
    }
}
