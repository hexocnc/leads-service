# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Spring Boot 4.1 / Java 25 microservice (`com.hexocnc.lead`) that stores sales leads in PostgreSQL. Leads arrive in two ways: an internal REST API (`/api/internal/leads`) and `LeadCreatedEvent` messages from RabbitMQ published by other Hexo services.

Shared contracts (`Domain`, `LeadCreatedEvent`, `LeadMessaging` exchange/routing-key constants) come from the `com.hexocnc:hexocnc-core` dependency, hosted in private GitHub Packages (`https://maven.pkg.github.com/hexocnc/*`). Building needs a `github` server entry in `~/.m2/settings.xml` with credentials that can read packages.

## Commands

- Build + tests: `mvn -B verify`
- Package jar (what the Dockerfile copies): `mvn -DskipTests package`
- Single test (once tests exist; currently `src/test` is absent): `mvn test -Dtest=ClassName#method`
- Run locally: `mvn spring-boot:run` — requires env vars `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` (see `.env`, `deploy/.env.example`); RabbitMQ defaults to `localhost:5672` guest/guest.
- Docker image: `mvn -DskipTests package && docker build -t leads-service .` (the image only copies `target/leads-service-*.jar`; it does not build it, because the private dependency needs auth).

## Architecture

Layers: `controller` → `service` (`LeadService`) → `repository` (Spring Data JPA), with static `LeadMapper` converting between request DTOs / `LeadCreatedEvent` / `LeadEntity` / `LeadResponseDto`.

**Idempotent event consumption** (spans several files): `LeadCreatedEventListener` → `LeadService.createLeadFromEvent` first calls `ProcessedEventRepository.insertIfAbsent("<domain>:<eventId>", now)`. If it returns 0 the event is a duplicate and is skipped; otherwise the lead is saved in the same transaction, so marker and lead are stored atomically. `ProcessedEventsCleanupJob` (cron, `app.processed-events.*` in `application.yaml`) purges markers older than the retention (default 1d) — so dedup only holds within that window.

**RabbitMQ**: `RabbitConfig` declares the durable direct exchange (name from `LeadMessaging`, must match producers), the queue `app.rabbit.lead-created.queue` (owned by this service), the binding, and a Jackson JSON converter. `default-requeue-rejected: false` means failed messages are dropped, not redelivered endlessly.

**Database**: schema is managed by Flyway (`src/main/resources/db/migration/V*.sql`); Hibernate runs with `ddl-auto: validate`, so any entity change requires a new migration. JDBC time zone is UTC.

## Deployment

GitHub Actions (`.github/workflows/deploy.yml`): `mvn verify` on PRs and master; on master it builds the jar, pushes the image to GHCR, then SSHes to the VPS and runs `deploy/remote-deploy.sh` with `deploy/docker-compose.prod.yml` (Postgres is bound to 127.0.0.1 on the same VPS). Logs go to `./logs/leadservice.log`.
