package com.example.vaultpostgres.service;

import org.springframework.core.env.MapPropertySource;

public interface VaultPropertyService {
    MapPropertySource reloadProperties();
}
