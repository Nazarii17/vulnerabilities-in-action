package com.ntj.viabackend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * DEMO: SQL Injection (CWE-89 / OWASP A03)
 * ============================================================
 *
 * Two endpoints doing the same thing (search users by username):
 *
 *   VULNERABLE  →  GET /api/demo/sql/vulnerable?username=alice
 *   FIXED       →  GET /api/demo/sql/secure?username=alice
 *
 * Attack payload to try on the VULNERABLE endpoint:
 *   ?username=' OR '1'='1
 *   → returns ALL rows, bypassing the filter
 *
 *   ?username=' OR '1'='1'--
 *   → classic comment-bypass
 * ============================================================
 */
@RestController
@RequestMapping("/api/demo/sql")
public class SqlInjectionController {

    private final JdbcTemplate jdbc;

    public SqlInjectionController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ----------------------------------------------------------------
    // ⚠️  VULNERABLE — string concatenation builds the SQL query
    //     Attack: ?username=' OR '1'='1   → dumps all users
    // ----------------------------------------------------------------
    @GetMapping("/vulnerable")
    public ResponseEntity<?> searchVulnerable(@RequestParam String username) {

        // ❌ VULNERABLE: user input directly concatenated into SQL
        // CWE-89: Improper Neutralisation of Special Elements used in an SQL Command
        String sql = "SELECT id, username, role, email FROM users WHERE username = '" + username + "'";

        try {
            List<Map<String, Object>> results = jdbc.queryForList(sql);
            return ResponseEntity.ok(Map.of(
                    "endpoint",     "VULNERABLE — SQL Injection",
                    "query_used",   sql,
                    "results",      results
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ----------------------------------------------------------------
    // ✅  FIXED — parameterised query, input never touches SQL syntax
    // ----------------------------------------------------------------
    @GetMapping("/secure")
    public ResponseEntity<?> searchSecure(@RequestParam String username) {

        // ✅ FIXED: JdbcTemplate parameterised query — input is always treated as data, never SQL
        // The '?' placeholder is bound safely by the JDBC driver
        String sql = "SELECT id, username, role, email FROM users WHERE username = ?";

        List<Map<String, Object>> results = jdbc.queryForList(sql, username);
        return ResponseEntity.ok(Map.of(
                "endpoint",   "SECURE — Parameterised Query",
                "results",    results
        ));
    }
}
