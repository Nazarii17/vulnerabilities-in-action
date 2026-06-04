# 🚀 CI/CD Quick Reference Guide

## For Developers - What Runs on Each Push/PR?

### **Automatic on Push to main/develop:**

| Workflow | Tool | Time | Output |
|----------|------|------|--------|
| 01 - Build & Test | Maven | ~2-3 min | JAR artifact + test results |
| 02 - SpotBugs SAST | SpotBugs + FindSecBugs | ~2 min | XML + HTML reports (Artifacts) |
| 03 - CodeQL | GitHub CodeQL | ~5-8 min | SARIF upload to Security tab |
| 00 - Summary | Report generation | ~1 min | Markdown summary with tables |

**Total Pipeline Time**: ~10-15 minutes

---

## 📍 Finding Your Reports

### **SpotBugs Reports**
1. Go to Actions → workflow run
2. Scroll to **Artifacts**
3. Download `spotbugs-report-html` (viewer-friendly)
4. Or `spotbugs-report-xml` (for parsing)

### **CodeQL Results**
1. Go to **Security** tab (top menu)
2. Click **Code scanning** (left sidebar)
3. View alerts grouped by severity
4. Click each alert to see vulnerable code + fix

### **Build Artifacts**
1. Go to Actions → workflow run
2. Download `build-artifacts`
3. Contains built JAR file

### **Complete Summary**
1. Go to Actions → 00-Summary-Report workflow
2. Click the completed run
3. Scroll to **Summary** section
4. See tables with all findings

---

## 🔍 What Each Scanner Finds

### **SpotBugs (Finds & Security)** ✅
```
Checks for:
- Hardcoded secrets (CWE-798)
- SQL injection patterns (CWE-89)
- Command injection patterns (CWE-78)
- Path traversal (CWE-22)
- XSS patterns (CWE-79)
- Weak cryptography
- And 100+ other bug patterns

Format: XML + HTML reports in Artifacts
Time to view: ~1 min
Updated: Every build
```

### **CodeQL (Deep Analysis)** 🔐
```
Checks for:
- Data flow vulnerabilities
- Control flow issues
- Security-specific patterns
- Complex taint analysis
- OWASP Top 10 issues

Format: Interactive alerts in Security tab
Time to view: Real-time in GitHub
Updated: Every push + weekly
```

---

## 🎯 Vulnerable Endpoints (For Testing)

Each endpoint here is **intentionally vulnerable** - there's a `/secure` variant showing the fix.

```
GET /api/demo/sql/vulnerable?username=' OR '1'='1
GET /api/demo/sql/secure?username=test
→ Demonstrates SQL injection and parameterized queries

GET /api/demo/xss/vulnerable?name=<script>alert('XSS')</script>
GET /api/demo/xss/secure?name=Test
→ Demonstrates XSS and HTML encoding

GET /api/demo/cmd/vulnerable?host=localhost;whoami
GET /api/demo/cmd/secure?host=localhost
→ Demonstrates command injection and validation

GET /api/demo/path/vulnerable?file=../../etc/passwd
GET /api/demo/path/secure?file=notes.txt
→ Demonstrates path traversal and containment checks

GET /api/demo/secrets/vulnerable
GET /api/demo/secrets/secure
→ Shows hardcoded secrets vs. environment variables

GET /api/demo/actuator/exposure
GET /actuator/env (vulnerable - misconfigured)
→ Shows actuator misconfiguration risks
```

---

## ✅ PR Checks & Branch Protection

### **What Appears on Pull Requests:**

✅ Build & Test - must pass
✅ SpotBugs Scan - shows summary (can warn or fail)
✅ CodeQL Scan - shows alerts if found

### **Can Block PRs On:**
- Build failures
- Test failures
- High/Critical vulnerabilities (if configured)

---

## 📊 Example Report Outputs

### **SpotBugs XML Report (Machine Readable)**
```xml
<BugCollection version="4.8.5">
  <BugInstance type="HARDCODED_PASSWORD" priority="1">
    <Class classname="com.ntj.viabackend.controller.HardcodedSecretsController"/>
    <Method name="vulnerableSecrets"/>
    <SourceLine startLine="50" endLine="50" message="Hard-coded credential"/>
  </BugInstance>
  ...
</BugCollection>
```

### **CodeQL Security Tab (Interactive)**
```
🔴 High severity: Potential SQL Injection
Location: SqlInjectionController.java:51
Severity: High
CWE: CWE-89
Flow: User input → String concatenation → SQL query

Recommendation: Use prepared statements with ?
Example: See /secure endpoint
```

### **Summary Report (Markdown Table)**
```
| Endpoint | Vulnerability | CWE | Scanner |
|----------|---|---|---|
| /api/demo/sql/vulnerable | SQL Injection | 89 | SpotBugs, CodeQL |
| /api/demo/xss/vulnerable | XSS | 79 | SpotBugs, CodeQL |
```

---

## 🚀 How to Test Locally

### **Run SpotBugs locally:**
```bash
cd via-backend
mvn clean compile spotbugs:spotbugs
# Open report at: target/spotbugs.html
```

### **Build locally:**
```bash
cd via-backend
mvn clean package
# Creates JAR at: target/via-backend-0.0.1-SNAPSHOT.jar
```

### **Run tests locally:**
```bash
cd via-backend
mvn test
# Results at: target/surefire-reports/
```

---

## 🐛 Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| No SpotBugs report | Compilation errors | Check build logs |
| CodeQL stuck | Long analysis | CodeQL can take 5-8 min |
| High memory usage | Large codebase | Normal for analysis tools |
| Timeout | Network issues | Re-run workflow |

---

## 📞 Quick Links

| Item | Link |
|------|------|
| Workflow Validation Report | `/docs/WORKFLOW-VALIDATION-COMPLETE.md` |
| SpotBugs Setup | `/docs/SAST-SETUP.md` |
| CodeQL Setup | `/docs/CODEQL-SETUP.md` |
| All Workflows | `/.github/workflows/` |
| Vulnerable Demo App | `/via-backend/src/main/java/com/ntj/viabackend/controller/` |

---

## ✨ Features Included

✅ Multi-tool SAST scanning (SpotBugs + CodeQL)
✅ Automatic report generation
✅ SARIF upload to GitHub Security
✅ PR integration ready
✅ Free tools only (no paid services)
✅ Comprehensive documentation
✅ HTML + XML report formats
✅ Weekly + on-push scanning schedules
✅ Beautiful summary reports with tables
✅ Artifact retention policies

---

**Last Updated**: June 2026
**Status**: ✅ Production Ready


