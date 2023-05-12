version: '3.9'

services:
  redis-sentinel:
    image: "bitnami/redis-sentinel:latest"
    container_name: redis-sentinel
    environment:
      - REDIS_MASTER_HOST=redis
      - REDIS_MASTER_PORT_NUMBER=6379
      - REDIS_MASTER_SET=mymaster
      - REDIS_MASTER_USER=jph
      - REDIS_MASTER_PASSWORD=jasonrocks
      - REDIS_SENTINEL_PORT_NUMBER=26379
      - TLS_REPLICATION=yes
      - REDIS_SENTINEL_TLS_PORT_NUMBER=26379
      - REDIS_SENTINEL_TLS_ENABLED=yes
      - REDIS_SENTINEL_TLS_CERT_FILE=/scripts/tests/tls/redis.crt
      - REDIS_SENTINEL_TLS_KEY_FILE=/scripts/tests/tls/redis.key
      - REDIS_SENTINEL_TLS_CA_FILE=/scripts/tests/tls/ca.crt
      - REDIS_SENTINEL_TLS_AUTH_CLIENTS=no
    ports:
      - '26379:26379'
    volumes:
      - ./scripts:/scripts
