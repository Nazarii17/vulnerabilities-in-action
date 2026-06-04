## 🔍 Semgrep SAST Analysis

### Tool
- **Semgrep** - Free, open-source static analysis engine
- **SARIF Upload** - Automatic upload to GitHub Security tab
- **Rule Sets** - Security Audit, OWASP Top 10, CWE Top 25
- **Language-Specific** - Java rules included

### What Gets Scanned
Semgrep detects:
- SQL Injection (CWE-89)
- Command Injection (CWE-78)
- Path Traversal (CWE-22)
- XSS (CWE-79)
- Hardcoded Secrets
- Weak Cryptography
- OWASP Top 10 patterns
- CWE Top 25 issues
- Custom security rules

### Configuration
**File**: `.github/workflows/04-sast-semgrep.yml`
- **Language**: Java
- **Rule Sets**:
  - `p/security-audit` - General security patterns
  - `p/owasp-top-ten` - OWASP Top 10 vulnerabilities
  - `p/cwe-top-25` - CWE Top 25 issues
  - `p/java` - Java-specific rules
- **Trigger**: 
  - Push to `main`/`develop`
  - All pull requests
  - Weekly schedule: Monday 3 AM UTC

### Results Location
**GitHub Security Tab** → **Code scanning**
- Automatically visible in repository **Security** tab
- Results grouped by severity and rule type
- Each finding links to vulnerable code with suggestions
- SARIF format for standard compliance

### Permissions Required
The workflow requires:
- `contents: read` - access to repository code
- `security-events: write` - permission to upload SARIF results

### Expected Findings
The demo app intentionally contains vulnerabilities that Semgrep will detect:
- Hardcoded secrets in `HardcodedSecretsController`
- SQL injection patterns in `SqlInjectionController`
- Command injection patterns in `CommandInjectionController`
- Path traversal in `PathTraversalController`
- XSS vulnerabilities in `XssController`

These are demonstrations - the `/secure` endpoints show proper fixes.

### Advantages of Semgrep
✅ Free and open-source
✅ Fast scanning (seconds, not minutes)
✅ High-quality rule sets
✅ Language-specific rules
✅ Easy configuration
✅ SARIF export for GitHub integration
✅ No account required
✅ Runs on-premises or in CI/CD

### CI/CD Integration
- Does NOT block the build by default
- Results available immediately (faster than CodeQL)
- Can be configured with branch protection rules
- Results available in real-time in GitHub
- SARIF file retained in workflow run
- JSON report available for detailed analysis

### Run Locally
Semgrep can be run locally:
```bash
# Install Semgrep
brew install semgrep  # macOS
# or
docker run -v "$PWD:/src" returntocorp/semgrep

# Run scan
semgrep --config=p/security-audit,p/owasp-top-ten via-backend/src
```

### Compare with Other SAST Tools

| Tool | Speed | Coverage | Free | Depth |
|------|-------|----------|------|-------|
| **Semgrep** | ⚡ Very Fast | Good | ✅ Yes | Medium |
| **SpotBugs** | Fast | General bugs | ✅ Yes | Medium |
| **CodeQL** | Slow | Excellent | ✅ Yes (public repos) | Deep |

Use Semgrep for quick feedback, CodeQL for comprehensive analysis!

