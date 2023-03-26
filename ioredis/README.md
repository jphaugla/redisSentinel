# RediSentinel with node.js


## Important Links/Notes
- [Redis with Node.js (ioredis)](https://docs.redis.com/latest/rs/references/client_references/client_ioredis/)
- [Developer Getting Started with Node and Redis (ioredis)](https://developer.redis.com/develop/node/gettingstarted/)

### Deploy redis enterprise
[see README.md in home directory](../README.md)

### Set Environment and Run
edit the [app.env](../scripts/app.env) appropriately for desires and environment
NOTE: enter the database username and password created in the [Manage Users](https://docs.redis.com/latest/rs/security/access-control/manage-users/) step
#### To get the SENTINEL_MASTER use redis cli to connect to the sentinal (8100) port and query for the sentinel information

```bash
[root@ip-172-16-32-11 ~]# redis-cli -p 8001 -h redis_enterprise_endpoint
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
'''

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

