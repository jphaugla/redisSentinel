# Spring Jedis

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
This github demonstrates jedis spring to connect to redis enterprise using TLS.  This is only a simple connection test to show jedis spring can use TLS to write to redis docker.

## Important Links
see parent directory [README.md important links](https://github.com/jphaugla/redisSentinel#important-links)

## Jedis code
This github shows code to connect to redis enterprise and redis-stack using sentinel and/or TLS.  Links provided in this github, show redisson sentinel with TLS as well.  Additional steps are needed on the redis enterprise server if sentinel is used with TLS-these steps are also provided.  Redis enterprise as a standalone redis can be used or a docker solution based on redis stack.   Each client tool is in a separate subdirectory with a separate README.md as the main directory holds all the docker-compose files.  This README covers the database deployment.  Trying to give as broad a set of working examples in the TLS and sentinel space with a variety of client tools such as jedis, spring jedis, lettuce, spring lettuce, python, and node.js.

## Using redis-stack
Redis stack (rs) has redis modules built in
### Deploy redis stack
see parent directory [README.md deploy on docker](https://github.com/jphaugla/redisSentinel#deploy-on-docker)
### Create trust and key store-rs
the java application needs a keystore and trustore for TLS.  This creates the keystore and trustfile using the keys files created in parent directory
```bash
cd redisSentinel/jedis-spring
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
docker-compose -f docker-compose.tls.yml -f jedis-spring/docker-compose-jedis-app.yml up -d
docker logs jedis-app
```
jedis app logs should contain
```bash
2023-06-02T19:50:18.240Z  INFO 7 --- [           main] c.r.j.service.RediSearchService          : Init RediSearchService
2023-06-02T19:50:18.240Z  INFO 7 --- [           main] c.r.j.service.RediSearchService          : starting redisearch.createTicker
2023-06-02T19:50:18.243Z  INFO 7 --- [           main] c.r.j.repository.TickerRepository        : starting tickerRepository.createTicker
2023-06-02T19:50:18.329Z  INFO 7 --- [           main] c.r.j.repository.TickerRepository        : tickerKey is ticker:TSLA.US:20230501
2023-06-02T19:50:18.584Z  INFO 7 --- [           main] c.r.j.service.RediSearchService          : return from redisearch.createTicker Success

2023-06-02T19:50:18.584Z  INFO 7 --- [           main] c.r.j.service.RediSearchService          : return val from createTicker Success
```

### Shut down docker
```bash
docker-compose -f docker-compose.tls.yml -f jedis-spring/docker-compose-jedis-app.yml down
```

## using redis-enterprise

### Deploy redis enterprise
see parent directory [README.md deploy on redis enterprise](https://github.com/jphaugla/redisSentinel#deploy-redis-enterprise)

### Create trust and key store
The java application needs a keystore and truststore for TLS.  This creates the keystore and trustfile using the keys files created in parent directory
Edit scripts/app.env for this environment
```bash
cd redisSentinel/jedis-spring
source scripts/app.env
./generatekeystore-RE.sh
./importkey-RE.sh
```
### Use Docker for application
#### Build docker application
edit  docker-compose-jedis-app-re.yml for environment variables
```bash
docker-compose -f docker-compose-jedis-app-re.yml build
```

#### Run docker application
```bash
docker-compose -f docker-compose-jedis-app-re.yml up -d
docker logs jedis-app
```
* should see Started RedisJediSpringApplication proccess running 
* verify data was written
```bash
cd ..
./redis-cli-re-tls.sh
keys *
```
* should see a key called *ticker:TSLA.US:20230501*

### Shutdown
```bash
cd jedis-spring
docker-compose -f docker-compose-jedis-app-re.yml down
```
### Run application outside of docker
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
java -jar target/jedisapp-0.0.1-SNAPSHOT.jar
```
* should see Started RedisJediSpringApplication proccess running 
* verify data was written
```bash
cd ..
./redis-cli-re-tls.sh
keys *
```
* should see a key called *ticker:TSLA.US:20230501*

### What happens
2023-05-01T09:52:38.486-05:00  INFO 24184 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2023-05-01T09:52:38.486-05:00  INFO 24184 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.7]
2023-05-01T09:52:38.575-05:00  INFO 24184 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2023-05-01T09:52:38.577-05:00  INFO 24184 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1611 ms
2023-05-01T09:52:38.728-05:00  INFO 24184 --- [           main] c.redis.jedispring.config.RedisConfig    : trust location=src/main/resources/tls/client-truststore.jks
2023-05-01T09:52:38.728-05:00  INFO 24184 --- [           main] c.redis.jedispring.config.RedisConfig    : key location=src/main/resources/tls/client-keystore.p12
2023-05-01T09:52:38.951-05:00  INFO 24184 --- [           main] c.redis.jedispring.config.RedisConfig    : parameters  host redis-10096.jphterra1.demo-rlec.redislabs.com port 10096 redis username jph redis password redis123 use tls true
2023-05-01T09:52:39.186-05:00  INFO 24184 --- [           main] c.r.j.repository.TickerRepository        : starting tickerRepository.createTicker
2023-05-01T09:52:39.252-05:00  INFO 24184 --- [           main] c.r.j.repository.TickerRepository        : tickerKey is ticker:TSLA.US:20230501
