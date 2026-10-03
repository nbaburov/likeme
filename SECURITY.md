# Security

## Status

LikeMe is an archived university project. It is not deployed anywhere and receives no feature work. Dependencies are pinned at the final patch of each line they were built on: Spring Boot 3.5, Java 21, Next.js 14.2 and MySQL 8.4.

That has two consequences you should know before running it anywhere other than your own machine:

- **Spring Boot 3.5 and Next.js 14 are out of upstream support.** Further security fixes for those lines will not arrive.
- **`npm audit` still reports advisories against `next` and the copy of `postcss` it bundles.** Every remaining fix requires Next.js 16, a breaking upgrade that is out of scope for an archive. The non-breaking audit fixes have been applied.

Treat the stack as a local demo. Do not expose it to the internet.

## Secrets

No credentials are committed. Every secret (database passwords, `JWT_SECRET`, `SENDGRID_API_KEY`) is read from the environment, and `.env.example` documents each one. The seeded demo accounts use a shared, public password on purpose; they exist only in the local database that `docker compose` creates.

## Reporting a vulnerability

If you find a problem in the code itself, for example an authorization gap in the API, please report it privately through [GitHub's private vulnerability reporting](https://github.com/nbaburov/likeme/security/advisories/new) rather than opening a public issue. Expect an acknowledgement within a week. Advisories that only restate the end-of-life status above will be closed with a pointer to this file.
