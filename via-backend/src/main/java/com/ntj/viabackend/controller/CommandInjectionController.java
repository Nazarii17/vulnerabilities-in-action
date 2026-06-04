package com.ntj.viabackend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ============================================================
 * DEMO: OS Command Injection (CWE-78 / OWASP A03)
 * ============================================================
 *
 * Two endpoints that "ping" a host:
 *
 *   VULNERABLE  →  GET /api/demo/cmd/vulnerable?host=localhost
 *   FIXED       →  GET /api/demo/cmd/secure?host=localhost
 *
 * Attack payload to try on the VULNERABLE endpoint:
 *   ?host=localhost; cat /etc/passwd
 *   ?host=localhost && whoami
 *   ?host=localhost | ls -la /
 *   → executes arbitrary OS commands beyond just ping
 * ============================================================
 */
@RestController
@RequestMapping("/api/demo/cmd")
public class CommandInjectionController {

    // ✅ FIXED: strict allowlist — only valid hostname/IP characters permitted
    private static final Pattern SAFE_HOST_PATTERN =
            Pattern.compile("^[a-zA-Z0-9.\\-]{1,253}$");

    // ----------------------------------------------------------------
    // ⚠️  VULNERABLE — user input appended directly into shell command string
    //     Attack: ?host=localhost; whoami   → runs TWO commands
    // ----------------------------------------------------------------
    @GetMapping("/vulnerable")
    public ResponseEntity<?> pingVulnerable(@RequestParam String host) {

        // ❌ VULNERABLE: shell=true equivalent — shell interprets metacharacters
        // CWE-78: Improper Neutralisation of Special Elements used in an OS Command
        // Attacker can inject: ; && | ` $() to chain arbitrary commands
        String command = "ping -c 2 " + host; // ❌ unsanitised input in command string

        try {
            Process process = Runtime.getRuntime().exec(command); // ❌ runs through shell
            String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
            return ResponseEntity.ok(Map.of(
                    "endpoint",       "VULNERABLE — Command Injection",
                    "command_run",    command,
                    "output",         output
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ----------------------------------------------------------------
    // ✅  FIXED — input validated + command passed as array (no shell)
    // ----------------------------------------------------------------
    @GetMapping("/secure")
    public ResponseEntity<?> pingSecure(@RequestParam String host) {

        // ✅ FIXED step 1: validate input against allowlist before use
        if (!SAFE_HOST_PATTERN.matcher(host).matches()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid host — only alphanumeric characters, dots and hyphens allowed"
            ));
        }

        // ✅ FIXED step 2: pass command as String[] array — OS executes directly, no shell parsing
        // Metacharacters like ; & | are treated as literal characters, not shell syntax
        String[] command = new String[]{"ping", "-c", "2", host};

        try {
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();
            List<String> lines = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines().collect(Collectors.toList());
            return ResponseEntity.ok(Map.of(
                    "endpoint", "SECURE — Validated Input + Array Exec",
                    "output",   lines
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
