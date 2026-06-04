package com.ntj.viabackend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * ============================================================
 * DEMO: Path Traversal (CWE-22 / OWASP A01)
 * ============================================================
 *
 * Two endpoints serving files from a safe base directory:
 *
 *   VULNERABLE  →  GET /api/demo/path/vulnerable?file=notes.txt
 *   FIXED       →  GET /api/demo/path/secure?file=notes.txt
 *
 * Attack payload to try on the VULNERABLE endpoint:
 *   ?file=../../etc/passwd
 *   ?file=../../../Windows/System32/drivers/etc/hosts
 *   → reads files OUTSIDE the intended directory
 * ============================================================
 */
@RestController
@RequestMapping("/api/demo/path")
public class PathTraversalController {

    // Intended safe directory — only files here should be readable
    private static final String BASE_DIR = System.getProperty("java.io.tmpdir") + "/via-demo/";

    public PathTraversalController() {
        // Create base dir + a sample file on startup for the demo
        try {
            Path dir = Paths.get(BASE_DIR);
            Files.createDirectories(dir);
            Path sampleFile = dir.resolve("notes.txt");
            if (!Files.exists(sampleFile)) {
                Files.writeString(sampleFile, "These are safe demo notes. Nothing secret here.");
            }
        } catch (IOException ignored) { }
    }

    // ----------------------------------------------------------------
    // ⚠️  VULNERABLE — filename joined directly, no validation
    //     Attack: ?file=../../etc/passwd  → escapes BASE_DIR
    // ----------------------------------------------------------------
    @GetMapping("/vulnerable")
    public ResponseEntity<?> readVulnerable(@RequestParam String file) {

        // ❌ VULNERABLE: user-supplied filename appended to base path without sanitisation
        // CWE-22: Improper Limitation of a Pathname to a Restricted Directory
        Path filePath = Paths.get(BASE_DIR + file);

        try {
            String content = Files.readString(filePath);
            return ResponseEntity.ok(Map.of(
                    "endpoint",    "VULNERABLE — Path Traversal",
                    "resolved_to", filePath.toString(),
                    "content",     content
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ----------------------------------------------------------------
    // ✅  FIXED — path normalised and confirmed to stay within BASE_DIR
    // ----------------------------------------------------------------
    @GetMapping("/secure")
    public ResponseEntity<?> readSecure(@RequestParam String file) {

        try {
            Path basePath     = Paths.get(BASE_DIR).toRealPath();
            // ✅ FIXED: resolve + normalise — eliminates any '../' sequences
            Path resolvedPath = basePath.resolve(file).normalize();

            // ✅ FIXED: canonical containment check — reject anything outside BASE_DIR
            if (!resolvedPath.startsWith(basePath)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Access denied: path traversal attempt detected"
                ));
            }

            String content = Files.readString(resolvedPath);
            return ResponseEntity.ok(Map.of(
                    "endpoint",    "SECURE — Path Validated",
                    "resolved_to", resolvedPath.toString(),
                    "content",     content
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
