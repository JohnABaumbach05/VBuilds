# VBuilds predeployment security

## CSRF protection

VBuilds authenticates using the HttpSession cookie. The RequestSecurityFilter
rejects state-changing /api/ requests unless their Origin matches the configured
public origin. If Origin is absent, a same-origin Referer is required. Requests
with neither header fail closed. This protects normal browser requests without
changing the JavaScript fetch calls. It is an Origin/Referer-based CSRF defense,
not a synchronizer-token implementation.

Local development defaults to http://localhost:8080. Open the site using that
exact URL (not 127.0.0.1). For production, set:

VBUILDS_PUBLIC_ORIGIN=https://your-actual-site.example
VBUILDS_COOKIE_SECURE=true

The origin must match the URL in the browser exactly, including scheme and port.
Do not add a trailing slash. HTTPS must be enabled at the public endpoint.
Do not configure CORS to allow arbitrary credentialed origins.

Session cookies are HttpOnly and SameSite=Lax. Secure cookies must be enabled
in production; the local HTTP default is intentionally false. Session timeout
is 30 minutes.

## Verification before release

- Run mvn test; confirm the security filter tests and existing tests pass.
- Login, logout, account updates, photo uploads, likes, comments, and admin
  actions should all still work from the configured origin.
- POST /api/builds from another Origin must return 403.
- POST /api/builds with no Origin and no Referer must return 403.
- Browser network tab: confirm session cookie has HttpOnly and SameSite=Lax;
  in production also confirm Secure.
- Review registration/login rate limiting, password reset/recovery, image
  content processing, secret handling, dependency scanning, and production logs.
- Check that the deployment platform provides HTTPS and that all secrets are
  configured as environment variables, not committed to GitHub.
- Do not expose PostgreSQL directly to the public internet.

These changes are a baseline, not a complete security audit.
