version: '3.9'

services:
  redis:
    image: redis/redis-stack:latest
    container_name: redis
    hostname: redis
    environment:
      REDIS_ARGS: "--tls-port 6379 --port 0 --tls-cert-file /scripts/sentinel_tests/tls/san.crt --tls-key-file /scripts/sentinel_tests/tls/private.key --tls-ca-cert-file /scripts/sentinel_tests/tls/CA-cert.pem --requirepass jasonrocks --aclfile /scripts/users.acl --tls-replication yes"
    ports:
      - '6379:6379'
      - '8001:8001'
    volumes:
      - ./redis_data:/data
      - ./scripts:/scripts
    networks:
      redissentinel_default:
        ipv4_address: 192.168.96.4
networks:
   redissentinel_default:
      ipam:
        driver: default
        config: 
          - subnet: 192.168.96.0/20
            gateway: 192.168.96.1
