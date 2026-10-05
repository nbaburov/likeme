#!/usr/bin/env bash
# Showcase CI: what "builds and runs from a clean checkout" means for this repository.
# Called by the shared workflow in nbaburov/.github; run it locally with `bash ci.sh`.
# Every check runs in a pinned container, so the result does not depend on the machine.
set -euo pipefail
cd "$(dirname "$0")"

net=likeme-ci
cleanup() { docker rm -f likeme-ci-db >/dev/null 2>&1 || true; docker network rm "$net" >/dev/null 2>&1 || true; }
trap cleanup EXIT

echo "== API tests"
docker network create "$net" >/dev/null
docker run -d --name likeme-ci-db --network "$net" -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=likeme \
  -e MYSQL_USER=likeme_mod -e MYSQL_PASSWORD=likeme mysql:8.4 >/dev/null
for _ in $(seq 1 60); do docker exec likeme-ci-db mysqladmin ping -h localhost -uroot -proot --silent >/dev/null 2>&1 && break; sleep 2; done
docker run --rm --network "$net" -v "$PWD/apps/api":/w -v /w/.gradle -v /w/build -w /w \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://likeme-ci-db:3306/likeme eclipse-temurin:21-jdk ./gradlew test --no-daemon
cleanup

echo "== Web lint"
docker run --rm -e CYPRESS_INSTALL_BINARY=0 -v "$PWD/apps/web":/w -v /w/node_modules -v /w/.next -w /w node:24 \
  sh -c "npm ci --no-audit --no-fund && npm run lint"

echo "== Stack boot"
perl -pe '
  s/^MYSQL_ROOT_PASSWORD=.*/"MYSQL_ROOT_PASSWORD=".unpack("H*", join "", map chr int rand 256, 1..24)/e;
  s/^MYSQL_PASSWORD=.*/"MYSQL_PASSWORD=".unpack("H*", join "", map chr int rand 256, 1..24)/e;
  s/^JWT_SECRET=.*/"JWT_SECRET=".unpack("H*", join "", map chr int rand 256, 1..48)/e;
' .env.example > .env
trap 'code=$?; [ $code -ne 0 ] && docker compose logs --no-color; docker compose down -v >/dev/null 2>&1; rm -f .env; cleanup; exit $code' EXIT
docker compose up -d --build
seed=$(docker wait "$(docker compose ps -aq seed)")
test "$seed" = "0"
for _ in $(seq 1 60); do curl -fsS -o /dev/null http://localhost:3000 && break; sleep 2; done
curl -fsS -o /dev/null http://localhost:3000

echo "== Demo logins"
for u in demo_admin demo_client demo_influencer; do
  status=$(curl -s -o /dev/null -w '%{http_code}' -H 'Content-Type: application/json' \
    -d "{\"username\":\"$u\",\"password\":\"demo1234\"}" http://localhost:8080/api/auth/login)
  echo "$u $status"
  test "$status" = "200"
done

echo "== End-to-end (Cypress)"
# Host networking: the app in the browser calls the API on localhost, as it would on a laptop.
docker run --rm --network host -v "$PWD/apps/web":/e2e -w /e2e cypress/included:13.17.0 --config baseUrl=http://localhost:3000
