package com.example.vaultpostgres.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.support.VaultResponse;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ApplicationRunnerConfig {
    private final VaultTemplate vaultTemplate;
    private final ConfigurableEnvironment environment;


    @Value("${vault.kv.backend}")
    private String kvBackend;
    @Value("${vault.kv.application-name}")
    private String kvApplicationName;

    @Bean
    public ApplicationRunner vaultSecretsRegistrar(SecretLeaseContainer container) {
        return args -> {
            container.addRequestedSecret(RequestedSecret.rotating(kvBackend + "/data/" + kvApplicationName));

            container.addLeaseListener(event ->
                    {
                        System.out.println("Lease Event: " + event);
                        VaultKeyValueOperations kv = vaultTemplate.opsForKeyValue(kvBackend, VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);
                        VaultResponse vaultResponse = kv.get(kvApplicationName);
                        if (vaultResponse != null && vaultResponse.getData() != null) {
                            Map<String, Object> data = vaultResponse.getData();
                            if (data != null) {
                                MapPropertySource vaultProps = new MapPropertySource("vault", data);
                                environment.getPropertySources().addFirst(vaultProps);
                            }
                        }
                    }
            );
        };
    }
}
