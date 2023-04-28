# Simple Jedis

## Outline

- [Overview](#overview)
- [Jedis code](#jedis-code)
- [Generate certs](#generate-certs)
- [Create trust and key store](#create-trust-and-key-store)
- [Build docker application](#build-docker-application)
- [Run full application](#run-full-application)
- [Shutdown](#shutdown)
## Overview
This github shows simple jedis code (not spring version) to connect to redis enterprise using TLS

## Important Links
see parent directory README.md
## Deploy redis
see parent directory README.md

### Deploy redis enterprise
see parent directory README.md

## Prepare database
see parent directory README.md

## Jedis code
This github shows simple jedis code (not spring version) to connect to redis enterprise using TLS.  This is only a simple connection test to print out a single word *bar* of output to verify the TLS connection from Jedis is used.

## Generate certs
see parent directory README.md

## Create trust and key store
the java application needs a keystore and trustore for TLS.  This creates the keystore and trustfile using the keys files created in parent directory
```bash
cd redisSentinel/simple-jedis
export TRUSTSTORE_PASSWORD=jasonrocks
export KEYSTORE_PASSWORD=jasonrocks
./generatekeystore.sh
./importkey.sh
```
## Build docker application
```bash
docker-compose -f docker-compose-jedis-app.yml
```

## Run full application
```bash
# back to parent directory
cd ..
docker-compose -f docker-compose.tls.yml -f simple-jedis/docker-compose-jedis-app.yml up -d
docker logs jedis-app
```
jedis app logs should say *bar*
## Shutdown
```bash
docker-compose -f docker-compose.tls.yml -f simple-jedis/docker-compose-jedis-app.yml down
```
