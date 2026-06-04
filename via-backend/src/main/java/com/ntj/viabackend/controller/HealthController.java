package com.ntj.viabackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired(required = false)
    private BuildProperties buildProperties;
    @Autowired
    private GitProperties gitProperties;


    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "name", applicationName,
                "time", Instant.now().toString()
        ));
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("name", applicationName);
        response.put("description", "Demo app showcasing SAST, DAST, SCA, TSS security scans");
        response.put("scans", "SAST | SCA (Snyk + OWASP), DAST (ZAP), TSS CI/BAU");

        Map<String, Object> buildInfo = new LinkedHashMap<>();
        if (buildProperties != null) {
            buildInfo.put("version", buildProperties.getVersion());
            buildInfo.put("artifact", buildProperties.getArtifact());
            buildInfo.put("group", buildProperties.getGroup());
            buildInfo.put("time", buildProperties.getTime() != null ? buildProperties.getTime().toString() : "N/A");
        } else {
            buildInfo.put("version", "N/A - run: mvn spring-boot:build-info");
        }
        response.put("build", buildInfo);

        Map<String, Object> gitInfo = new LinkedHashMap<>();
        if (gitProperties != null) {
            gitInfo.put("branch", gitProperties.getBranch());
            gitInfo.put("commit-id", gitProperties.getShortCommitId());
            gitInfo.put("commit-time", gitProperties.getCommitTime());
        } else {
            gitInfo.put("status", "N/A - no git repository detected");
        }
        response.put("git", gitInfo);

        return ResponseEntity.ok(response);
    }
}