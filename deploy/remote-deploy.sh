#!/usr/bin/env bash
# Виконується НА VPS (його викликає GitHub Actions через ssh) або вручну.
# Змінні: APP_IMAGE, APP_TAG (образ для запуску), GHCR_USER; токен реєстру — у stdin.
# Секрети сервісу лежать у .env поруч (поза git).
set -euo pipefail
cd "$(dirname "$0")"

docker network inspect hexo-infra >/dev/null 2>&1 \
  || { echo "Немає Docker-мережі hexo-infra: спершу запустіть infrastructure (docker compose up -d)." >&2; exit 1; }

docker login ghcr.io -u "$GHCR_USER" --password-stdin

printf 'APP_IMAGE=%s\nAPP_TAG=%s\n' "$APP_IMAGE" "$APP_TAG" > image.env
env_args=(--env-file image.env)
[ -f .env ] && env_args=(--env-file .env --env-file image.env)
compose() { docker compose -f docker-compose.prod.yml "${env_args[@]}" "$@"; }

compose pull app
docker logout ghcr.io
compose up -d --remove-orphans

# чекаємо, поки основний сервіс стане healthy; якщо ні — показуємо логи й падаємо (попередня версія вже зупинена,
# тож відкат — повторний запуск деплою старого коміту)
cid="$(compose ps -q app)"
for _ in $(seq 1 60); do
  status="$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}}' "$cid")"
  [ "$status" = "healthy" ] && { echo "==> OK: $APP_TAG задеплоєно"; docker image prune -f >/dev/null; compose ps; exit 0; }
  sleep 5
done
echo "!! Сервіс не став healthy за 5 хв. Останні логи:" >&2
compose logs --tail 80 app >&2
exit 1
