# Spring Jedis Sentinel

## Outline

- [Overview](#overview)
- [Jedis code](#jedis-code)
- [Using redis-stack](#using-redis-stack)
  - [Create trust and key store](#create-trust-and-key-store-rs)
  - [Build docker application](#build-docker-application-rs)
  - [Run docker application](#run-docker-application)
- [Using redis enterprise](#using-redis-enterprise)
  - [Create trust and key store](#create-trust-and-key-store-rs)
  - [Build docker application](#build-docker-application-rs)
  - [Run docker application](#run-docker-application-rs)
- [Run application outside of docker](#run-application-outside-of-docker)
  - [Install java](#install-java)
    - [redhat](#redhat)
    - [ubuntu](#ubuntu)
  - [Compile application](#compile-application)
  - [Run](#run)
- [Shutdown](#shutdown)
## Overview
This subdirectory demonstrates jedis spring to connect to redis enterprise or redis stack using TLS and sentinel

## Important Links
see parent directory [README.md important links](https://github.com/jphaugla/redisSentinel#important-links)

## Jedis code
This github demonstrates jedis spring sentinel to connect to redis enterprise using TLS.  

## Using redis-stack  (not tested yet)
Redis stack (rs) has redis modules built in
### Deploy redis stack
see parent directory [README.md deploy on docker](https://github.com/jphaugla/redisSentinel#deploy-on-docker)
### Create trust and key store-rs
the java application needs a keystore and trustore for TLS.  This creates the keystore and trustfile using the keys files created in parent directory
```bash
cd redisSentinel/sentinel-jedis-spring
export TRUSTSTORE_PASSWORD=jasonrocks
export KEYSTORE_PASSWORD=jasonrocks
./generatekeystore.sh
./importkey.sh
```
### Build docker application-rs
```bash
docker-compose -f docker-compose-jedis-app.yml build
```

### Run docker application-rs
```bash
# back to parent directory
cd ..
docker-compose -f docker-compose.tls.yml -f docker-compose.sentinel-tls.sh -f sentinel-jedis-spring/docker-compose-jedis-app.yml up -d
docker logs jedis-app
```
jedis app logs should say *bar*

### Shut down docker
```bash
docker-compose -f docker-compose.tls.yml -f sentinel-jedis-spring/docker-compose-jedis-app.yml down
```

## using redis-enterprise

### Deploy redis enterprise
Do this in two pieces, first verify sentinel is working without TLS by following [these steps in the parent README]( https://github.com/jphaugla/redisSentinel/tree/main#deploy-redis-enterprise-database-without-tls)

### Run application without docker

#### Install Java
* redhat
  * install java
  * set java home
```bash
sudo yum install java-18-openjdk
export JAVA_HOME=/usr/lib/jvm/java-18-openjdk
```
  * download and install maven following [these steps](https://linuxize.com/post/how-to-install-apache-maven-on-centos-7) - NOTE:  yum installs older version
  * this worked with java 18
```bash
export JAVA_HOME=/usr/lib/jvm/java-18-openjdk
export M2_HOME=/opt/maven
export MAVEN_HOME=/opt/maven
export PATH=${M2_HOME}/bin:${PATH}
```

* ubuntu
```bash
mkdir binaries
cd binaries
apt install openjdk-18-jdk openjdk-18-jre
cat <<EOF | sudo tee /etc/profile.d/jdk18.sh
export JAVA_HOME=/usr/lib/jvm/java-18-openjdk-amd64
EOF
```
  * download and install maven following [these steps](https://phoenixnap.com/kb/install-maven-on-ubuntu)  Note:  apt-get installs older version
#### Compile application
```bash
mvn clean package
```
#### Run
edit the [app.env](scripts/app.env) appropriately for desires and environment.  Note example values for docker or redis enterprise
NOTE: enter the database username and password created in the [Manage Users](https://docs.redis.com/latest/rs/security/access-control/manage-users/) step
```bash
source scripts/app.env
target/redisentinel-0.0.1-SNAPSHOT.jar
```
* should see Started RedisSentinelApplication proccess running 
* verify data was written
```bash
cd ..
./redis-cli-re-no-tls-un.sh
keys *
```
* should see a key called *ticker:TSLA.US:20230501*

#### What happens
2023-05-01T12:07:01.391-05:00  INFO 38790 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.7]
2023-05-01T12:07:01.474-05:00  INFO 38790 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2023-05-01T12:07:01.476-05:00  INFO 38790 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1674 ms
2023-05-01T12:07:01.628-05:00  INFO 38790 --- [           main] com.redis.sentinel.config.RedisConfig    : parameters  host redis-10096.jphterra1.demo-rlec.redislabs.com sentinel port 8001 sentinel master db1 redis username jph redis password redis123 use tls false
2023-05-01T12:07:01.766-05:00  INFO 38790 --- [           main] redis.clients.jedis.JedisSentinelPool    : Trying to find master from available Sentinels...
2023-05-01T12:07:02.049-05:00  INFO 38790 --- [           main] redis.clients.jedis.JedisSentinelPool    : Redis master running at 13.56.173.237:10096, starting Sentinel listeners...
2023-05-01T12:07:02.052-05:00  INFO 38790 --- [           main] redis.clients.jedis.JedisSentinelPool    : Created JedisSentinelPool to master at 13.56.173.237:10096
2023-05-01T12:07:02.104-05:00  INFO 38790 --- [           main] c.r.sentinel.service.RediSearchService   : Init RediSearchService
2023-05-01T12:07:02.391-05:00  INFO 38790 --- [           main] c.r.sentinel.service.RediSearchService   : return val from createTicker Success
2023-05-01T12:07:03.886-05:00  INFO 38790 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 5000 (https) with context path ''
2023-05-01T12:07:03.901-05:00  INFO 38790 --- [           main] c.r.sentinel.RedisSentinelApplication    : Started RedisSentinelApplication in 4.682 seconds (process running for 5.326)

#### Enable Sentinel TLS on redis enterprise
[Setup TLS sentinel redis enterprise](https://github.com/jphaugla/redisSentinel/tree/main#setup-tls-for-redis-enterprise)

### Create trust and key store
The java application needs a keystore and truststore for TLS.  This creates the keystore and trustfile using the keys files created in parent directory
Edit scripts/app.env for this environment
```bash
cd redisSentinel/sentinel-jedis-spring
source scripts/app.env
./generatekeystore-RE.sh
./importkey-RE.sh
```
```bash
source scripts/app.env
target/redisentinel-0.0.1-SNAPSHOT.jar
```
* should see Started RedisSentinelApplication proccess running 
* verify data was written
```bash
cd ..
./redis-cli-re-no-tls-un.sh
keys *
```
* should see a key called *ticker:TSLA.US:20230501*
#### with TLS results
2023-05-01T16:07:32.419-05:00  INFO 61245 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 5000 (https)
2023-05-01T16:07:32.441-05:00  INFO 61245 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2023-05-01T16:07:32.441-05:00  INFO 61245 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.7]
2023-05-01T16:07:32.543-05:00  INFO 61245 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2023-05-01T16:07:32.545-05:00  INFO 61245 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1865 ms
2023-05-01T16:07:32.711-05:00  INFO 61245 --- [           main] com.redis.sentinel.config.RedisConfig    : trust location=src/main/resources/tls/client-truststore.jks
2023-05-01T16:07:32.711-05:00  INFO 61245 --- [           main] com.redis.sentinel.config.RedisConfig    : key location=src/main/resources/tls/client-keystore.p12
2023-05-01T16:07:32.711-05:00  INFO 61245 --- [           main] com.redis.sentinel.config.RedisConfig    : trust pw=jasonrocks
2023-05-01T16:07:32.711-05:00  INFO 61245 --- [           main] com.redis.sentinel.config.RedisConfig    : key pw=jasonrocks
2023-05-01T16:07:32.951-05:00  INFO 61245 --- [           main] com.redis.sentinel.config.RedisConfig    : parameters  host redis-10096.jphterra1.demo-rlec.redislabs.com sentinel port 8001 sentinel master db1 redis username jph redis password redis123 use tls true
2023-05-01T16:07:33.132-05:00  INFO 61245 --- [           main] redis.clients.jedis.JedisSentinelPool    : Trying to find master from available Sentinels...
2023-05-01T16:07:33.650-05:00  INFO 61245 --- [           main] redis.clients.jedis.JedisSentinelPool    : Redis master running at 13.56.173.237:10096, starting Sentinel listeners...
2023-05-01T16:07:33.657-05:00  INFO 61245 --- [           main] redis.clients.jedis.JedisSentinelPool    : Created JedisSentinelPool to master at 13.56.173.237:10096
2023-05-01T16:07:33.721-05:00  INFO 61245 --- [           main] c.r.sentinel.service.RediSearchService   : Init RediSearchService
2023-05-01T16:07:34.119-05:00  INFO 61245 --- [           main] c.r.sentinel.service.RediSearchService   : return val from createTicker Success

2023-05-01T16:07:35.398-05:00  INFO 61245 --- [           main] o.a.t.util.net.NioEndpoint.certificate   : Connector [https-jsse-nio-5000], TLS virtual host [_default_], certificate type [UNDEFINED] configured from [file:/Users/jasonhaugland/gits/redisSentinel/sentinel-jedis-spring/src/main/resources/tls/client-keystore.p12] using alias [tomcat] and with trust store [file:/Users/jasonhaugland/gits/redisSentinel/sentinel-jedis-spring/src/main/resources/tls/client-truststore.jks]
2023-05-01T16:07:35.421-05:00  INFO 61245 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 5000 (https) with context path ''
2023-05-01T16:07:35.441-05:00  INFO 61245 --- [           main] c.r.sentinel.RedisSentinelApplication    : Started RedisSentinelApplication in 5.448 seconds (process running for 6.253)
