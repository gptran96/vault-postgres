package com.example.vaultpostgres.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.vault.authentication.AppRoleAuthentication;
import org.springframework.vault.authentication.AppRoleAuthenticationOptions;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.client.RestTemplateBuilder;
import org.springframework.vault.client.VaultClients;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.client.VaultEndpointProvider;
import org.springframework.vault.config.AbstractVaultConfiguration;

import java.net.URI;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class VaultConfig extends AbstractVaultConfiguration {
    @Value("${vault.uri}")
    private String vaultUri;
    @Value("${vault.namespace}")
    private String namespace;
    @Value("${vault.app-role.role-id}")
    private String roleId;
    @Value("${vault.app-role.secret-id}")
    private String secretId;

    @Bean
    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.from(URI.create(vaultUri));
    }

    @Bean
    @Override
    public ClientAuthentication clientAuthentication() {
        AppRoleAuthenticationOptions options = AppRoleAuthenticationOptions.builder().roleId(AppRoleAuthenticationOptions.RoleId.provided(roleId)).secretId(AppRoleAuthenticationOptions.SecretId.provided(secretId)).build();
        return new AppRoleAuthentication(options, restOperations());
    }

    @Override
    protected RestTemplateBuilder restTemplateBuilder(VaultEndpointProvider endpointProvider, ClientHttpRequestFactory requestFactory) {
        return super.restTemplateBuilder(endpointProvider, requestFactory).customizers(restTemplate -> restTemplate.getInterceptors()
                .add(VaultClients.createNamespaceInterceptor(namespace))
        );
    }
}
