## 🔐 CodeQL SAST Analysis

### Tool
- **GitHub CodeQL** - Advanced security analysis tool from GitHub
- **SARIF Upload** - Automated upload to GitHub Security tab
- **Query Suite** - security-and-quality (includes CWE, OWASP Top 10)

### What Gets Scanned
CodeQL detects:
- SQL Injection (CWE-89)
- Command Injection (CWE-78)
- Path Traversal (CWE-22)
- XSS (CWE-79)
- Hardcoded Secrets
- Weak Cryptography
- And many more security patterns

### Configuration
**File**: `.github/workflows/03-sast-codeql.yml`
- **Trigger**: 
  - Push to `main`/`develop`
  - All pull requests
  - Weekly schedule: Monday 2 AM UTC
- **Language**: Java (auto-detected)
- **Query Suite**: security-and-quality (GitHub recommended)

### Results Location
**GitHub Security Tab** → **Code scanning**
- Automatically visible in repository **Security** tab
- Results grouped by severity: Critical, High, Medium, Low
- Each alert links to the vulnerable code with:
  - Source file and line number
  - CWE reference
  - Suggested fix
  - Similar patterns in codebase

### Permissions Required
The workflow requires:
- `contents: read` - access to repository code
- `security-events: write` - permission to upload SARIF results

### Expected Findings
The demo app intentionally contains vulnerabilities that CodeQL will detect:
- Hardcoded secrets in `HardcodedSecretsController`
- SQL injection patterns in `SqlInjectionController`
- Command injection patterns in `CommandInjectionController`
- Path traversal in `PathTraversalController`
- XSS vulnerabilities in `XssController`

These are demonstrations - the `/secure` endpoints show proper fixes.

### CI/CD Integration
- Does NOT block the build by default (warnings/informational)
- Can be configured with branch protection rules to require passing checks
- Results available for 90 days in GitHub
- SARIF file retained in workflow run (if needed for compliance)

