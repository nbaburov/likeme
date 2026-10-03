FROM node:24-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --no-audit --no-fund
COPY . .
# NEXT_PUBLIC_* values are inlined into the browser bundle at build time.
ARG NEXT_PUBLIC_API_URL=http://localhost:8080/api
ARG NEXT_PUBLIC_TOKEN_EXPIRATION=86400000
ARG NEXT_PUBLIC_REFRESH_TOKEN_EXPIRATION=604800000
ARG NEXT_PUBLIC_TOKEN_KEY=likeme_access_token
ARG NEXT_PUBLIC_REFRESH_TOKEN_KEY=likeme_refresh_token
ENV NEXT_PUBLIC_API_URL=$NEXT_PUBLIC_API_URL \
    NEXT_PUBLIC_TOKEN_EXPIRATION=$NEXT_PUBLIC_TOKEN_EXPIRATION \
    NEXT_PUBLIC_REFRESH_TOKEN_EXPIRATION=$NEXT_PUBLIC_REFRESH_TOKEN_EXPIRATION \
    NEXT_PUBLIC_TOKEN_KEY=$NEXT_PUBLIC_TOKEN_KEY \
    NEXT_PUBLIC_REFRESH_TOKEN_KEY=$NEXT_PUBLIC_REFRESH_TOKEN_KEY \
    NEXT_TELEMETRY_DISABLED=1
RUN npm run build

FROM node:24-alpine
WORKDIR /app
ENV NODE_ENV=production NEXT_TELEMETRY_DISABLED=1 HOSTNAME=0.0.0.0 PORT=3000
COPY --from=build --chown=node:node /app/.next/standalone ./
COPY --from=build --chown=node:node /app/.next/static ./.next/static
COPY --from=build --chown=node:node /app/public ./public
USER node
EXPOSE 3000
CMD ["node", "server.js"]
