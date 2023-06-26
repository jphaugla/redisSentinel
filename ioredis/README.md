# RediSentinel with node.js


## Important Links/Notes
- [Redis with Node.js (ioredis)](https://docs.redis.com/latest/rs/references/client_references/client_ioredis/)
- [Dockerizing node.js app](https://nodejs.org/en/docs/guides/nodejs-docker-webapp)
- [Developer Getting Started with Node and Redis (ioredis)](https://developer.redis.com/develop/node/gettingstarted/)

## Deploy on redis enterprise or docker compose
### Deploy on Docker
* may need to adjust environment variables in the docker compose file for the environment
* ACL is also set in the Docker environment variable section
* To add TLS, look at additional parameters in [bitnami sentinel environment variables](https://hub.docker.com/r/bitnami/redis-sentinel/)
* To add TLS to redis-stack, follow same technique used to the password and define the ACL
    * These are just setting variables in the redis.conf file
    * TLS has a set of redis.conf variables needed as well
* must run the application under docker compose because of sentinel networking
    * sentinel redirects to the redis database and all three nodes (redis, redis sentinel, and Node app) must be on same network
* Because we have the redis stack and sentinel servers configured in the parent directory, it's best to run docker-compose from there and include the ioredis compose file

### Without Sentinel, with TLS
```bash
docker-compose -f ioredis/docker-compose-no-sentinel.yml build 
docker-compose docker-compose.tls.yml -f ioredis/docker-compose-no-sentinel.yml up -d
```

To test if it ran correctly print the logs:
```bash
docker logs ioredis-app
```

To shut down the containers:
```bash
docker-compose docker-compose.tls.yml -f ioredis/docker-compose-no-sentinel.yml down
```
### With Sentinel, without TLS
The first thing you'll want to do is change the ioredis/Dockerfile to run the `sentinel_login` application, so uncomment it out
and comment out the other ones.

Next, build the client
```bash
docker-compose -f ioredis/docker-compose-sentinel.yml build
```

Next, run redis, sentinel and client containers
```bash
docker-compose -f docker-compose.no-tls.yml -f docker-compose.sentinel-no-tls.yml -f ioredis/docker-compose-sentinel.yml up -d
```

### With Sentinel, with TLS
Now comes the fun part - with Sentinel and with TLS

First, follow the steps to configure sentinel with TLS in the project root README

Next, change the ioredis/Dockerfile to run the `sentinel_login_tls` application, so uncomment it out
and comment out the other ones.

Next, build the client
```bash
docker-compose -f ioredis/docker-compose-sentinel.yml build
```

Next, run redis, sentinel and client containers with TLS
```bash
docker-compose -f docker-compose.redis-sentinel-keys.yml -f docker-compose.sentinel-tls.yml -f ioredis/docker-compose-sentinel.yml up -d
```

### Deploy redis enterprise
[see README.md in home directory](../README.md)
#### To get the SENTINEL_MASTER use redis cli to connect to the sentinel (8001 or 26379) port and query for the sentinel information

```bash
# using redis enterprise
[root@ip-172-16-32-11 ~]# redis-cli -p 8001 -h redis_enterprise_endpoint
# using docker
[root@ip-172-16-32-11 ~]# redis-cli -p 26379 -h localhost
127.0.0.1:8001> SENTINEL masters
1) 1) “name”
  2) “TestDB@internal”
  3) “ip”
  4) “172.16.32.11"
  5) “port”
  6) “12000"
  7) “flags”
  8) “master”
  9) “num-other-sentinels”
  10) “0"
2) 1) “name”
  2) “TestDB”
  3) “ip”
  4) “3.239.252.137"
  5) “port”
  6) “12000"
  7) “flags”
  8) “master”
  9) “num-other-sentinels”
  10) “0"
```

#### Set Environment and Run
this is to run against redis enterprise
edit the [app.env](../scripts/app.env) appropriately for desires and environment
NOTE: enter the database username and password created in the [Manage Users](https://docs.redis.com/latest/rs/security/access-control/manage-users/) step


```bash
source ../scripts/app.env
```
#### run npm jobs	
* Make sure node and npm are installed 
```bash
npm -v
node -v
```
* Run init and install ioredis
```bash
npm init
npm i -s ioredis
```
* run standard redis
```bash
node simple_login.js
```
* run sentinel redis
```bash
node sentenel_login.js
```
### What happens
When the code starts the redis enterprise endpoint (environment variable is *REDIS_HOST*) is used for the server with the redis enterprise sentinel port of 8100.  This is log from the code as each of the sentinal masters is resolved:
```bash
2023-03-24T16:48:13.771-05:00  INFO 76995 --- [           main] redis.clients.jedis.JedisSentinelPool    : Trying to find master from available Sentinels...
2023-03-24T16:48:14.010-05:00  INFO 76995 --- [           main] redis.clients.jedis.JedisSentinelPool    : Redis master running at 54.241.107.136:12128, starting Sentinel listeners...
2023-03-24T16:48:14.015-05:00  INFO 76995 --- [           main] redis.clients.jedis.JedisSentinelPool    : Created JedisSentinelPool to master at 54.241.107.136:12128
2023-03-24T16:48:14.094-05:00  INFO 76995 --- [           main] c.r.sentinel.service.RediSearchService   : Init RediSearchService
2023-03-24T16:48:14.095-05:00  INFO 76995 --- [           main] c.r.sentinel.service.RediSearchService   : redisPassword is jasonrocks
2023-03-24T16:48:14.097-05:00  INFO 76995 --- [           main] redis.clients.jedis.JedisSentinelPool    : Trying to find master from available Sentinels...
2023-03-24T16:48:14.252-05:00  INFO 76995 --- [           main] redis.clients.jedis.JedisSentinelPool    : Redis master running at 54.241.107.136:12128, starting Sentinel listeners...
2023-03-24T16:48:14.253-05:00  INFO 76995 --- [           main] redis.clients.jedis.JedisSentinelPool    : Created JedisSentinelPool to master at 54.241.107.136:12128
2023-03-24T16:48:14.254-05:00  INFO 76995 --- [           main] c.r.sentinel.service.RediSearchService   : looging in using username jph
``` 

