package com.example.vaultpostgres.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/vault")
@RequiredArgsConstructor
public class VaultController {
    private final Environment environment;

    @GetMapping("/view-config")
    public Map<String, String> hello() {
        Map<String, String> map = new HashMap<>();
        map.put("username1", environment.getProperty("username1"));
        return map;
    }
}
