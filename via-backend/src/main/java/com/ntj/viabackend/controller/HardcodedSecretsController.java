package com.ntj.viabackend.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ============================================================
 * DEMO: Hardcoded Secrets (CWE-798 / OWASP A07)
 * ============================================================
 *
 *   VULNERABLE  →  GET /api/demo/secrets/vulnerable
 *   FIXED       →  GET /api/demo/secrets/secure
 *
 * The VULNERABLE version exposes hardcoded credentials directly
 * in source code — detectable by SAST scanners and Snyk Code.
 *
 * The FIXED version reads values from environment variables /
 * application properties — secrets never appear in the codebase.
 * ============================================================
 */
@RestController
@RequestMapping("/api/demo/secrets")
public class HardcodedSecretsController {

    // ✅ FIXED: injected from environment variable or application.yaml
    // Set via:  export APP_API_KEY=my-real-secret
    // Or in application.yaml:  app.api-key: ${APP_API_KEY:not-configured}
    @Value("${app.api-key:not-configured}")
    private String apiKeyFromConfig;

    @Value("${app.db-password:not-configured}")
    private String dbPasswordFromConfig;

    // ----------------------------------------------------------------
    // ⚠️  VULNERABLE — credentials hardcoded in source code
    //     SAST tools (CodeQL, SpotBugs, Snyk Code) will flag these
    // ----------------------------------------------------------------
    @GetMapping("/vulnerable")
    public ResponseEntity<?> vulnerableSecrets() {

        // ❌ VULNERABLE: hardcoded credentials
        // CWE-798: Use of Hard-coded Credentials
        // Anyone with access to the source code or JAR file can extract these
        String hardcodedApiKey    = "sk-prod-rbc-api-key-1234567890abcdef"; // ❌ hardcoded API key
        String hardcodedPassword  = "P@ssw0rd_Super_Secret_123!";           // ❌ hardcoded password
        String hardcodedDbUrl     = "jdbc:postgresql://prod-db.rbc.com:5432/corebanking" +
                "?user=admin&password=P@ssw0rd_Super_Secret_123!"; // ❌ credentials in URL

        return ResponseEntity.ok(Map.of(
                "endpoint",         "VULNERABLE — Hardcoded Secrets",
                "warning",          "These values are visible in source code, git history, and the compiled JAR",
                "api_key_exposed",  hardcodedApiKey,
                "password_exposed", hardcodedPassword,
                "db_url_exposed",   hardcodedDbUrl
        ));
    }

    // ----------------------------------------------------------------
    // ✅  FIXED — all sensitive values come from environment / config
    // ----------------------------------------------------------------
    @GetMapping("/secure")
    public ResponseEntity<?> secureSecrets() {

        // ✅ FIXED: values injected at runtime from environment variables
        // The source code never contains the actual secret values
        // In GitHub Actions, these are stored as encrypted Repository Secrets
        return ResponseEntity.ok(Map.of(
                "endpoint",      "SECURE — Secrets from Environment",
                "api_key",       maskSecret(apiKeyFromConfig),
                "db_password",   maskSecret(dbPasswordFromConfig),
                "how_to_set",    "Set APP_API_KEY and APP_DB_PASSWORD as environment variables or GitHub Secrets"
        ));
    }

    /** Masks a secret for safe display — shows only first 4 chars */
    private String maskSecret(String secret) {
        if (secret == null || secret.length() <= 4) return "****";
        return secret.substring(0, 4) + "****";
    }
}

