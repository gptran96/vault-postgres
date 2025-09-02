package com.example.vaultpostgres.config;

import com.example.vaultpostgres.service.VaultPropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;

import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class VaultPropertyLoader {
    private final VaultPropertyService vaultPropertyService;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");

    @Value("${vault.kv.backend}")
    private String kvBackend;
    @Value("${vault.kv.application-name}")
    private String kvApplicationName;

    @Bean
    public MapPropertySource vaultPropertySource() {
        return vaultPropertyService.reloadProperties();
    }

    @Bean
    public ApplicationRunner vaultSecretsRegistrar(SecretLeaseContainer container) {
        return args -> {
            container.addRequestedSecret(RequestedSecret.rotating(kvBackend + "/" + kvApplicationName));

            container.addLeaseListener(event -> {
                        log.info("Lease Event: {} time: {}", event, sdf.format(new Date()));
                        vaultPropertyService.reloadProperties();
                    }
            );
        };
    }
}