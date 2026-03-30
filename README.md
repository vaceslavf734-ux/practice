# Security Report

## Overview
Security analysis of the Course Management application using SAST (Semgrep), SCA (OWASP Dependency-Check), and DAST (OWASP ZAP).

## Findings

### Critical

| # | Vulnerability | Location | OWASP | Fix |
|---|---|---|---|---|
| 1 | Plain-text passwords | `AuthController.java`, `data.sql` | A02:2021 | BCrypt hashing via `BCryptPasswordEncoder` |
| 2 | Remote code execution via `PluginLoader` | `PluginLoader.java` | A08:2021 | Disabled remote class loading |

### High

| # | Vulnerability | Location | OWASP | Fix |
|---|---|---|---|---|
| 3 | SQL Injection | `CourseService.java` | A03:2021 | Parameterized query (`?` placeholder) |
| 4 | XXE Injection | `XmlController.java` | A05:2021 | Disabled DTD, external entities in SAXReader |
| 5 | SSRF in ProxyController | `ProxyController.java` | A10:2021 | Validate host, block loopback/private IPs |
| 6 | SSRF in CourseController | `CourseController.java` | A10:2021 | Same SSRF protection as above |
| 7 | Broken access control (role assignment) | `AuthController.java` | A01:2021 | Hardcoded STUDENT role on registration |

### Medium

| # | Vulnerability | Location | OWASP | Fix |
|---|---|---|---|---|
| 8 | Permissive CORS (`*`) | `WebConfig.java`, Controllers | A05:2021 | Restricted to `/api/**`, specific methods |
| 9 | Exposed H2 console | `application.yaml` | A05:2021 | `h2.console.enabled: false` |
| 10 | Open actuator endpoints | `application.yaml` | A05:2021 | Limited to `health,info` |
| 11 | Stacktrace exposure | `application.yaml` | A04:2021 | `include-stacktrace: NEVER` |
| 12 | SQL logging enabled | `application.yaml` | A09:2021 | `show-sql: false` |
| 13 | Vulnerable dom4j 1.6.1 | `pom.xml` | A06:2021 | Updated to `org.dom4j:dom4j:2.1.4` |
| 14 | Password logged in plain text | `AuthController.java` | A09:2021 | Removed password from log message |

## Tools Used

- **SAST**: Semgrep with `p/default` ruleset
- **SCA**: OWASP Dependency-Check 12.1.0
- **DAST**: OWASP ZAP (Baseline Scan + API Scan)

## False Positives

Suppressed via `.semgrepignore` (test files) and `dependency-check-suppression.xml` (Spring Boot managed dependencies).
