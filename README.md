# RediSentinel with Jedis and node.js

## Outline

- [Overview](#overview)
- [Where is ioredis](#ioredis)
- [Important Links](#important-linksnotes)
- [Deploy redis](#deploy-redis)
  - [Deploy on docker](#deploy-on-docker)
  - [Deploy Redis Enterprise](#deploy-redis-enterprise)
- [Prepare database](#prepare-database)
- [Verify Sentinel](#verify-sentinel)
- [Jedis code](#jedis-code)
  - [Install java](#install-java)
    - [redhat](#redhat)
    - [ubuntu](#ubuntu)
  - [Compile application](#compile-application)
  - [Run](#run)
  - [What happens](#what-happens)
 - [TLS](#tls)

## Overview
This github shows code to connect to redis enterprise using sentinel with ioredis and jedis.  Links provided in this github, show redisson sentinel with TLS as well.  Additionally, additional steps are needed on the redis enterprise server if sentinel is used with TLS-these steps are also provided.


## ioredis
Where is node.js (ioredis)
look in subdirectory called ioredis.  There is a README.md there as well

## Important Links
- [Redisson github including sentinel steps](https://github.com/jphaugla/Redis-Digital-Banking-redisson/)
- [GitHub deploying this java application with redis enterprise on AWS](https://github.com/jphaugla/tfmodule-aws-redis-enterprise)
- [Generate a self-signed SSL certificate for IP address](https://medium.com/@antelle/how-to-generate-a-self-signed-ssl-certificate-for-an-ip-address-f0dd8dddf754)
- [Redis CLI with tls](https://redis.io/docs/ui/cli)
- [SSL/TLS with Redis Enterprise blog](https://tgrall.github.io/blog/2020/01/02/how-to-use-ssl-slash-tls-with-redis-enterprise/)
- [Redis Sentinel](https://redis.io/docs/management/sentinel/)
- [Redis spring boot with sentinel](https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#redis:sentinel)
- [ioredis with TLS](https://github.com/luin/ioredis#sentinel)
- [bitnami sentinal environment variables](https://hub.docker.com/r/bitnami/redis-sentinel/)
## Deploy redis
### Deploy on Docker
* may need to adjust environment variables in the docker compose file for the environment
* ACL is also set in the Docker environment variable section
* To add TLS, look at additional parameters in [bitnami sentinal environment variables](https://hub.docker.com/r/bitnami/redis-sentinel/)
* To add TLS to redis-stack, follow same technique used to the password and define the ACL
  * These are just setting variables in the redis.conf file
  * TLS has a set of redis.conf variables neeeded as well
* must run the application under docker compose because of sentinel networking
  * sentinel redirects to the redis database and all three nodes (redis, redis sentinel, and java app) must be on same network
```bash
docker-compose build 
docker-compose up -d
```

### Deploy redis enterprise
In [this github]((https://github.com/jphaugla/tfmodule-aws-redis-enterprise)), a database is created with redis search and redis json deployed.  json is needed if the environment variable WRITE_JSON is set to true.  Search is not used in this application but if curious about search, the code is *lifted* from [my github that uses search and json](https://github.com/jphaugla/redisearchStock).

## Prepare database
Need to create an ACL for the database to be used as the login for the application.  The following steps cover doing this:
* [Configure ACLs](https://docs.redis.com/latest/rs/security/access-control/configure-acl/)
* [Create Roles](https://docs.redis.com/latest/rs/security/access-control/create-roles/)
* [Manage Users](https://docs.redis.com/latest/rs/security/access-control/manage-users/)

## Verify sentinel 
Get the SENTINEL_MASTER use redis cli to connect to the sentinal (8100) port and query for the sentinel information

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

## Jedis code
This github is about using redis sentinel with Redis Enterprise.  Within the code is a [Jedis spring sentinel connection](https://github.com/jphaugla/redisSentinel/blob/main/src/main/java/com/redis/sentinel/config/RedisConfig.java) and a [Jedis non-spring jedis connection](https://github.com/jphaugla/redisSentinel/blob/main/src/main/java/com/redis/sentinel/service/RediSearchService.java#L76).  The non-spring connection is used for doing the redisjson commands.  To deploy redis enterprise on AWS, use [this github](https://github.com/jphaugla/tfmodule-aws-redis-enterprise)

### Install Java
#### redhat
  * install java 
  * set java home
```bash
sudo yum install java-17-openjdk
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```
  * download and install maven following [these steps](https://linuxize.com/post/how-to-install-apache-maven-on-centos-7) - NOTE:  yum installs older version
  * this worked with java 17
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export M2_HOME=/opt/maven
export MAVEN_HOME=/opt/maven
export PATH=${M2_HOME}/bin:${PATH}
```

#### ubuntu
```bash
mkdir binaries
cd binaries
apt install openjdk-18-jdk openjdk-18-jre
cat <<EOF | sudo tee /etc/profile.d/jdk18.sh
export JAVA_HOME=/usr/lib/jvm/java-18-openjdk-amd64
EOF
```
  * download and install maven flollowing [these steps](https://phoenixnap.com/kb/install-maven-on-ubuntu)  Note:  apt-get installs older version
### Compile application
```bash
mvn clean package
```

### Run
edit the [app.env](scripts/app.env) appropriately for desires and environment.  Note example values for docker or redis enterprise
NOTE: enter the database username and password created in the [Manage Users](https://docs.redis.com/latest/rs/security/access-control/manage-users/) step
```bash
source scripts/app.env
java -jar target/redisentinel-0.0.1-SNAPSHOT.jar
```
#### To run
use the scripts directory, add some data
```bash
cd scripts
./postTicker.sh
./getKey.sh
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

## TLS
Using sentinel and TLS with Redis Enterprise becomes a bit more difficult.  The given proxy key must be replaced with a proxy key that allows traffic through the redis enterprise endpoint as well as the redis enterprise IPs.  This is needed because when sentinel (actually the Redis Enterprise Discover service) retrieves the redis enterprise server, the IPs and not the DNS name is retrieved.

The techniques described in [generate a self-signed SSL certificate for IP address github](https://medium.com/@antelle/how-to-generate-a-self-signed-ssl-certificate-for-an-ip-address-f0dd8dddf754) are used below.  Review the link for more in-depth explanations.

### Redis Server steps
Log into a redis enterprise node
* Check the current key using openssl to get the information for the cnf file running this on redis server
```bash
 openssl s_client -connect jphterra2.demo-rlec.redislabs.com:8443 </dev/null 2>/dev/null | openssl x509 -noout -text | grep DNS
```
* copy the ./ssl/san.cnf file up the the redis server and save in a temp dir under /opt/redislabs/bin
```bash
sudo bash
cd /opt/redislabs/bin
mkdir temp
cd temp
```
copy contents of san.cnf into a file int this temp directory
* using the DNS lines, edit the san.cnf to have correct DNS and IP addresses
* create new key and cert pem files
```bash
openssl req -x509 -nodes -days 730 -newkey rsa:2048 -keyout key.pem -out cert.pem -config san.cnf
```
Make redis server updates using rladmin and supervisorctl
```bash
cd /opt/redislabs/bin
./rladmin
rladmin> cluster config sentinel_ssl_policy allowed
Cluster configured successfully
rladmin> cluster certificate set proxy certificate_file temp/cert.pem key_file temp/key.pem
Set proxy certificate to contents of file temp/cert.pem
Set proxy key to contents of file temp/key.pem
exit
./supervisorctl restart dmcproxy
./supervisorctl restart sentinel_service
```
Verify the sentinel service has the IPs in the certificate
```bash
 openssl s_client -connect jphterra2.demo-rlec.redislabs.com:8001 </dev/null 2>/dev/null | openssl x509 -noout -text | grep IP
```
 
go back to the redisson github for the testing of TLS with REdisson

copy this generated cert key at /opt/redislabs/bin/temp/cert.key to the connecting client in the redisson github directory
