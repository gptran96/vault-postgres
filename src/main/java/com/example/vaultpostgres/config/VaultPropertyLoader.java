package com.example.vaultpostgres.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class VaultPropertyLoader {
    private final VaultTemplate vaultTemplate;
    private final ConfigurableEnvironment environment;
    @Value("${vault.kv.backend}")
    private String kvBackend;
    @Value("${vault.kv.application-name}")
    private String kvApplicationName;

    @Bean
    public MapPropertySource vaultPropertySource() {
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