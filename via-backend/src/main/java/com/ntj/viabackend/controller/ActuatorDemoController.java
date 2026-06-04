package com.ntj.viabackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**

 ============================================================
 DEMO: Actuator Misconfiguration (CWE-200 / OWASP A05)
 ============================================================ *
 This controller explains what Actuator exposes in the
 VULNERABLE config and what the FIXED config locks down. *
 Informational endpoint:
 GET /api/demo/actuator/exposure *
 Real (dangerous) actuator endpoints active in this demo:
 GET /actuator → lists all exposed endpoints
 GET /actuator/env → ❌ dumps ALL environment variables + values
 GET /actuator/heapdump → ❌ downloads full JVM heap (contains tokens/passwords)
 GET /actuator/beans → ❌ full Spring application context
 GET /actuator/mappings → ❌ all URL routes (reconnaissance goldmine)
 GET /actuator/loggers → ❌ read + modify log levels at runtime
 GET /actuator/health → shows internal component health details
 GET /actuator/metrics → internal performance counters
 ============================================================
 */
@RestController
@RequestMapping("/api/demo/actuator")
public class ActuatorDemoController {

    @GetMapping("/exposure")
    public ResponseEntity<?> exposureReport() {

        // Build a clear picture of what's exposed and why it's dangerous
        Map<String, Object> vulnerableEndpoints = new LinkedHashMap<>();
        vulnerableEndpoints.put("/actuator/env",
                "❌ CRITICAL — dumps all environment variables including DB passwords, API keys, and tokens");
        vulnerableEndpoints.put("/actuator/heapdump",
                "❌ CRITICAL — downloads full JVM heap; attackers can extract secrets from memory");
        vulnerableEndpoints.put("/actuator/loggers",
                "❌ HIGH — allows changing log verbosity at runtime; can expose sensitive data in logs");
        vulnerableEndpoints.put("/actuator/beans",
                "❌ HIGH — reveals full internal application structure; helps attacker map the system");
        vulnerableEndpoints.put("/actuator/mappings",
                "❌ HIGH — lists ALL URL routes; attacker uses this for reconnaissance");
        vulnerableEndpoints.put("/actuator/health",
                "⚠️  MEDIUM — with show-details=always, reveals DB status, disk space, component names");
        vulnerableEndpoints.put("/actuator/metrics",
                "⚠️  LOW — internal counters; reveals traffic patterns and resource usage");

        Map<String, Object> fix = new LinkedHashMap<>();
        fix.put("step_1", "Expose only: management.endpoints.web.exposure.include=health");
        fix.put("step_2", "Set health details to never: management.endpoint.health.show-details=never");
        fix.put("step_3", "Move to a separate internal port: management.server.port=8081");
        fix.put("step_4", "Firewall port 8081 — block all external traffic, allow only internal monitoring tools");
        fix.put("step_5", "Add Spring Security to require authentication on /actuator/** if port separation is not possible");

        return ResponseEntity.ok(Map.of(
                "demo",                "Actuator Misconfiguration — CWE-200 / OWASP A05",
                "current_config",      "⚠️  VULNERABLE — all endpoints exposed on public port 8080",
                "dangerous_endpoints", vulnerableEndpoints,
                "how_to_fix",          fix,
                "try_it",              List.of(
                        "GET /actuator/env      → see all environment variables",
                        "GET /actuator/beans    → see full Spring context",
                        "GET /actuator/mappings → see all URL routes",
                        "GET /actuator/heapdump → download JVM heap"
                )
        ));
    }
}