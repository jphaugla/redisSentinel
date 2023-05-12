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
    ports:
      - '26379:26379'
