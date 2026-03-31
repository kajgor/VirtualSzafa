# VirtualSzafa

Starter produktu VirtualSzafa przygotowany pod architekturę:
- backend: Kotlin + Ktor
- shared domain: Kotlin Multiplatform
- storage docelowo: PostgreSQL + S3 compatible object storage
- mobile docelowo: Android + iOS

## Moduły
- `backend` – REST API
- `shared:shared-core` – część współdzielona
- `shared:shared-wardrobe` – model garderoby
- `shared:shared-outfits` – model outfitów

## Uruchomienie backendu

```bash
./gradlew :backend:run
```

## Endpointy
- `GET /health`
- `GET /api/v1/items`
