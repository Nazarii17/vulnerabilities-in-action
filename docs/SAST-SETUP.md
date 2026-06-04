## 🔒 SAST (Static Application Security Testing) Setup

### Tools Used
- **SpotBugs 4.8.5.0** - General bug detection
- **FindSecBugs 1.13.0** - Security-focused bug detection

### What Gets Scanned
- SQL Injection risks (CWE-89)
- Command Injection risks (CWE-78)
- Path Traversal risks (CWE-22)
- XSS risks (CWE-79)
- Hardcoded Secrets (CWE-798)
- Weak Cryptography
- And many more security patterns

### Configuration
**File**: `via-backend/pom.xml`
- **Effort**: `max` - most thorough analysis
- **Threshold**: `medium` - reports medium and above severity
- **Output**: `target/spotbugsXml.xml`

### Run Locally
```bash
cd via-backend
mvn clean verify spotbugs:spotbugs
```

The XML report will be generated at: `target/spotbugsXml.xml`

### CI/CD Integration
Workflow: `.github/workflows/02-sast-spotbugs.yml`
- Runs on: push to `main`/`develop` and all PRs
- Reports uploaded as artifact for 30 days
- Summary logged to GitHub Actions
- Does NOT block the build (continues on error for analysis)

### Expected Findings
The demo app is **intentionally vulnerable**, so the scan will detect:
- Hardcoded secrets in `HardcodedSecretsController`
- SQL injection patterns in `SqlInjectionController`
- Command injection patterns in `CommandInjectionController`
- And other security anti-patterns

These are demonstrations - the `/secure` endpoints show proper fixes.

